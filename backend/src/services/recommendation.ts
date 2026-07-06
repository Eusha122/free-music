import type { PrismaClient, Track } from "@prisma/client";

// Simple content-based "For You" recommender: look at genres from the
// user's likes + recent plays, rank unheard tracks by genre overlap.
// No ML infra needed to start; swap in collaborative filtering later once
// there's enough cross-user listening data to make it worthwhile.
export async function recommendFor(
  prisma: PrismaClient,
  userId: string,
  limit = 30
): Promise<Track[]> {
  const [likes, recentPlays] = await Promise.all([
    prisma.like.findMany({ where: { userId }, include: { track: true }, take: 100 }),
    prisma.playHistory.findMany({
      where: { userId },
      include: { track: true },
      orderBy: { playedAt: "desc" },
      take: 50,
    }),
  ]);

  const seedTracks = [...likes.map((l) => l.track), ...recentPlays.map((p) => p.track)];
  const heardTrackIds = new Set(seedTracks.map((t) => t.id));

  const genreWeight = new Map<string, number>();
  for (const t of seedTracks) {
    for (const g of t.genres) {
      genreWeight.set(g, (genreWeight.get(g) ?? 0) + 1);
    }
  }

  if (genreWeight.size === 0) {
    // Cold start: no history yet, just return a broad recent sample.
    return prisma.track.findMany({ take: limit, orderBy: { createdAt: "desc" } });
  }

  const topGenres = [...genreWeight.entries()]
    .sort((a, b) => b[1] - a[1])
    .slice(0, 8)
    .map(([g]) => g);

  const candidates = await prisma.track.findMany({
    where: { genres: { hasSome: topGenres }, id: { notIn: [...heardTrackIds] } },
    take: limit * 3,
  });

  const scored = candidates
    .map((t) => ({
      track: t,
      score: t.genres.reduce((sum, g) => sum + (genreWeight.get(g) ?? 0), 0),
    }))
    .sort((a, b) => b.score - a.score)
    .slice(0, limit);

  return scored.map((s) => s.track);
}

// Smart shuffle: avoid repeating the same artist back-to-back and
// de-prioritize tracks the user played very recently.
export function smartShuffle(tracks: Track[], recentlyPlayedIds: Set<string>): Track[] {
  const pool = [...tracks];
  const result: Track[] = [];
  let lastArtist: string | null = null;

  while (pool.length > 0) {
    const candidates = pool
      .map((t, idx) => ({ t, idx }))
      .filter(({ t }) => t.artistName !== lastArtist);
    const pickFrom = candidates.length > 0 ? candidates : pool.map((t, idx) => ({ t, idx }));

    const weighted = pickFrom.map(({ t, idx }) => ({
      idx,
      weight: recentlyPlayedIds.has(t.id) ? 0.2 : 1,
    }));
    const totalWeight = weighted.reduce((s, w) => s + w.weight, 0);
    let r = Math.random() * totalWeight;
    let chosenIdx = weighted[0].idx;
    for (const w of weighted) {
      r -= w.weight;
      if (r <= 0) {
        chosenIdx = w.idx;
        break;
      }
    }

    const [chosen] = pool.splice(chosenIdx, 1);
    result.push(chosen);
    lastArtist = chosen.artistName;
  }

  return result;
}

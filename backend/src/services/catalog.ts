import type { PrismaClient, Track } from "@prisma/client";
import type { NormalizedTrack } from "../sources/types.js";
import { searchJamendo, popularJamendo } from "../sources/jamendo.js";
import { searchAudius, trendingAudius } from "../sources/audius.js";

// Upserts normalized tracks from upstream sources into our DB so they get a
// stable internal id (needed for likes/playlists/history) without us ever
// hosting the audio ourselves - streamUrl always points back to the source.
export async function upsertTracks(
  prisma: PrismaClient,
  tracks: NormalizedTrack[]
): Promise<Track[]> {
  return Promise.all(
    tracks.map((t) =>
      prisma.track.upsert({
        where: { source_sourceId: { source: t.source, sourceId: t.sourceId } },
        create: t,
        update: {
          title: t.title,
          artistName: t.artistName,
          albumName: t.albumName,
          durationSec: t.durationSec,
          streamUrl: t.streamUrl,
          artworkUrl: t.artworkUrl,
          genres: t.genres,
          license: t.license,
        },
      })
    )
  );
}

export async function search(prisma: PrismaClient, query: string): Promise<Track[]> {
  const [jamendo, audius] = await Promise.allSettled([
    searchJamendo(query),
    searchAudius(query),
  ]);
  const results: NormalizedTrack[] = [
    ...(jamendo.status === "fulfilled" ? jamendo.value : []),
    ...(audius.status === "fulfilled" ? audius.value : []),
  ];
  return upsertTracks(prisma, results);
}

export async function browseHome(prisma: PrismaClient): Promise<Track[]> {
  const [jamendo, audius] = await Promise.allSettled([popularJamendo(15), trendingAudius(15)]);
  const results: NormalizedTrack[] = [
    ...(jamendo.status === "fulfilled" ? jamendo.value : []),
    ...(audius.status === "fulfilled" ? audius.value : []),
  ];
  // Interleave sources so the home feed isn't dominated by whichever source
  // resolved first.
  const shuffled = interleave(
    results.filter((t) => t.source === "jamendo"),
    results.filter((t) => t.source === "audius")
  );
  return upsertTracks(prisma, shuffled);
}

function interleave<T>(a: T[], b: T[]): T[] {
  const out: T[] = [];
  const max = Math.max(a.length, b.length);
  for (let i = 0; i < max; i++) {
    if (a[i]) out.push(a[i]);
    if (b[i]) out.push(b[i]);
  }
  return out;
}

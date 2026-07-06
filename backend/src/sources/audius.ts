import type { NormalizedTrack } from "./types.js";

const APP_NAME = process.env.AUDIUS_APP_NAME ?? "FreeTuneApp";

let cachedHost: string | null = null;
let hostFetchedAt = 0;
const HOST_TTL_MS = 30 * 60 * 1000;

// Audius is a decentralized network: discover a live node from the public
// registry instead of hardcoding one, since any single node can go down.
async function getHost(): Promise<string> {
  if (cachedHost && Date.now() - hostFetchedAt < HOST_TTL_MS) return cachedHost;
  const res = await fetch("https://api.audius.co");
  if (!res.ok) throw new Error(`Failed to fetch Audius host list: ${res.status}`);
  const data = (await res.json()) as { data: string[] };
  if (!data.data?.length) throw new Error("No Audius hosts available");
  cachedHost = data.data[Math.floor(Math.random() * data.data.length)];
  hostFetchedAt = Date.now();
  return cachedHost;
}

interface AudiusTrack {
  id: string;
  title: string;
  user: { name: string };
  duration: number;
  artwork?: { "480x480"?: string };
  genre?: string;
  mood?: string;
}

interface AudiusResponse {
  data: AudiusTrack[];
}

function normalize(t: AudiusTrack, host: string): NormalizedTrack {
  const genres = [t.genre, t.mood].filter((g): g is string => !!g).map((g) => g.toLowerCase());
  return {
    source: "audius",
    sourceId: t.id,
    title: t.title,
    artistName: t.user?.name ?? "Unknown Artist",
    albumName: null,
    durationSec: t.duration,
    streamUrl: `${host}/v1/tracks/${t.id}/stream?app_name=${encodeURIComponent(APP_NAME)}`,
    artworkUrl: t.artwork?.["480x480"] ?? null,
    genres,
    license: null,
  };
}

export async function searchAudius(query: string, limit = 20): Promise<NormalizedTrack[]> {
  const host = await getHost();
  const url = new URL(`${host}/v1/tracks/search`);
  url.searchParams.set("query", query);
  url.searchParams.set("app_name", APP_NAME);
  url.searchParams.set("limit", String(limit));
  const res = await fetch(url);
  if (!res.ok) throw new Error(`Audius search failed: ${res.status}`);
  const data = (await res.json()) as AudiusResponse;
  return data.data.map((t) => normalize(t, host));
}

export async function trendingAudius(limit = 20): Promise<NormalizedTrack[]> {
  const host = await getHost();
  const url = new URL(`${host}/v1/tracks/trending`);
  url.searchParams.set("app_name", APP_NAME);
  url.searchParams.set("limit", String(limit));
  const res = await fetch(url);
  if (!res.ok) throw new Error(`Audius trending failed: ${res.status}`);
  const data = (await res.json()) as AudiusResponse;
  return data.data.map((t) => normalize(t, host));
}

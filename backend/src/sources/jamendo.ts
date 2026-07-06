import type { NormalizedTrack } from "./types.js";

const JAMENDO_CLIENT_ID = process.env.JAMENDO_CLIENT_ID ?? "";
const BASE_URL = "https://api.jamendo.com/v3.0";

interface JamendoTrack {
  id: string;
  name: string;
  artist_name: string;
  album_name: string;
  duration: number;
  audio: string;
  image: string;
  license_ccurl: string;
  musicinfo?: { tags?: { genres?: string[] } };
}

interface JamendoResponse {
  headers: { status: string; code: number };
  results: JamendoTrack[];
}

function normalize(t: JamendoTrack): NormalizedTrack {
  return {
    source: "jamendo",
    sourceId: t.id,
    title: t.name,
    artistName: t.artist_name,
    albumName: t.album_name || null,
    durationSec: t.duration,
    streamUrl: t.audio,
    artworkUrl: t.image || null,
    genres: (t.musicinfo?.tags?.genres ?? []).map((g) => g.toLowerCase()),
    license: t.license_ccurl ?? null,
  };
}

async function jamendoRequest(params: Record<string, string>): Promise<NormalizedTrack[]> {
  if (!JAMENDO_CLIENT_ID) {
    throw new Error(
      "JAMENDO_CLIENT_ID is not set. Get a free client id at https://devportal.jamendo.com/"
    );
  }
  const url = new URL(`${BASE_URL}/tracks/`);
  url.searchParams.set("client_id", JAMENDO_CLIENT_ID);
  url.searchParams.set("format", "json");
  url.searchParams.set("include", "musicinfo");
  url.searchParams.set("audioformat", "mp32");
  for (const [k, v] of Object.entries(params)) url.searchParams.set(k, v);

  const res = await fetch(url);
  if (!res.ok) throw new Error(`Jamendo request failed: ${res.status}`);
  const data = (await res.json()) as JamendoResponse;
  return data.results.map(normalize);
}

export function searchJamendo(query: string, limit = 20): Promise<NormalizedTrack[]> {
  return jamendoRequest({ search: query, limit: String(limit) });
}

export function browseJamendoByTag(tag: string, limit = 20): Promise<NormalizedTrack[]> {
  return jamendoRequest({ fuzzytags: tag, limit: String(limit), order: "popularity_total" });
}

export function popularJamendo(limit = 20): Promise<NormalizedTrack[]> {
  return jamendoRequest({ limit: String(limit), order: "popularity_month" });
}

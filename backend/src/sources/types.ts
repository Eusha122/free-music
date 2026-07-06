export interface NormalizedTrack {
  source: "jamendo" | "audius";
  sourceId: string;
  title: string;
  artistName: string;
  albumName: string | null;
  durationSec: number;
  streamUrl: string;
  artworkUrl: string | null;
  genres: string[];
  license: string | null;
}

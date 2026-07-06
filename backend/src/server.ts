import Fastify from "fastify";
import cors from "@fastify/cors";
import prismaPlugin from "./plugins/prisma.js";
import authPlugin from "./plugins/auth.js";
import authRoutes from "./routes/auth.js";
import trackRoutes from "./routes/tracks.js";
import playlistRoutes from "./routes/playlists.js";

const app = Fastify({ logger: true });

await app.register(cors, { origin: true });
await app.register(prismaPlugin);
await app.register(authPlugin);
await app.register(authRoutes);
await app.register(trackRoutes);
await app.register(playlistRoutes);

app.get("/health", async () => ({ status: "ok" }));

const port = Number(process.env.PORT ?? 3000);
app.listen({ port, host: "0.0.0.0" }).catch((err) => {
  app.log.error(err);
  process.exit(1);
});

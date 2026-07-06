import type { FastifyInstance } from "fastify";
import { z } from "zod";

export default async function playlistRoutes(app: FastifyInstance) {
  app.addHook("preHandler", app.requireAuth);

  app.get("/playlists", async (req, reply) => {
    const playlists = await app.prisma.playlist.findMany({
      where: { userId: req.userId! },
      include: { tracks: { include: { track: true }, orderBy: { position: "asc" } } },
    });
    return reply.send({ playlists });
  });

  app.post("/playlists", async (req, reply) => {
    const parsed = z.object({ name: z.string().min(1) }).safeParse(req.body);
    if (!parsed.success) return reply.code(400).send({ error: "Missing name" });
    const playlist = await app.prisma.playlist.create({
      data: { name: parsed.data.name, userId: req.userId! },
    });
    return reply.code(201).send({ playlist });
  });

  app.post("/playlists/:id/tracks", async (req, reply) => {
    const { id } = req.params as { id: string };
    const parsed = z.object({ trackId: z.string().min(1) }).safeParse(req.body);
    if (!parsed.success) return reply.code(400).send({ error: "Missing trackId" });

    const playlist = await app.prisma.playlist.findFirst({
      where: { id, userId: req.userId! },
    });
    if (!playlist) return reply.code(404).send({ error: "Playlist not found" });

    const count = await app.prisma.playlistTrack.count({ where: { playlistId: id } });
    const entry = await app.prisma.playlistTrack.create({
      data: { playlistId: id, trackId: parsed.data.trackId, position: count },
    });
    return reply.code(201).send({ entry });
  });

  app.delete("/playlists/:id/tracks/:trackId", async (req, reply) => {
    const { id, trackId } = req.params as { id: string; trackId: string };
    const playlist = await app.prisma.playlist.findFirst({
      where: { id, userId: req.userId! },
    });
    if (!playlist) return reply.code(404).send({ error: "Playlist not found" });

    await app.prisma.playlistTrack.deleteMany({ where: { playlistId: id, trackId } });
    return reply.code(204).send();
  });

  app.delete("/playlists/:id", async (req, reply) => {
    const { id } = req.params as { id: string };
    await app.prisma.playlist.deleteMany({ where: { id, userId: req.userId! } });
    return reply.code(204).send();
  });
}

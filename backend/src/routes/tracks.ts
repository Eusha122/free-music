import type { FastifyInstance } from "fastify";
import { z } from "zod";
import { search, browseHome } from "../services/catalog.js";
import { recommendFor, smartShuffle } from "../services/recommendation.js";

export default async function trackRoutes(app: FastifyInstance) {
  app.get("/tracks/search", async (req, reply) => {
    const parsed = z.object({ q: z.string().min(1) }).safeParse(req.query);
    if (!parsed.success) return reply.code(400).send({ error: "Missing query param 'q'" });
    const tracks = await search(app.prisma, parsed.data.q);
    return reply.send({ tracks });
  });

  app.get("/tracks/home", async (_req, reply) => {
    const tracks = await browseHome(app.prisma);
    return reply.send({ tracks });
  });

  app.get(
    "/tracks/recommendations",
    { preHandler: app.requireAuth },
    async (req, reply) => {
      const tracks = await recommendFor(app.prisma, req.userId!);
      return reply.send({ tracks });
    }
  );

  app.get(
    "/tracks/shuffle-queue",
    { preHandler: app.requireAuth },
    async (req, reply) => {
      const parsed = z.object({ trackIds: z.string() }).safeParse(req.query);
      if (!parsed.success) return reply.code(400).send({ error: "Missing trackIds" });
      const ids = parsed.data.trackIds.split(",").filter(Boolean);
      const tracks = await app.prisma.track.findMany({ where: { id: { in: ids } } });

      const recentPlays = await app.prisma.playHistory.findMany({
        where: { userId: req.userId! },
        orderBy: { playedAt: "desc" },
        take: 30,
        select: { trackId: true },
      });
      const recentIds = new Set(recentPlays.map((p) => p.trackId));

      return reply.send({ tracks: smartShuffle(tracks, recentIds) });
    }
  );

  // Records a play so recommendations/shuffle can learn from it. Playback
  // itself streams directly from the source's streamUrl on the client -
  // we never proxy or re-host the audio.
  app.post(
    "/tracks/:id/play",
    { preHandler: app.requireAuth },
    async (req, reply) => {
      const { id } = req.params as { id: string };
      await app.prisma.playHistory.create({ data: { userId: req.userId!, trackId: id } });
      return reply.code(204).send();
    }
  );

  app.post(
    "/tracks/:id/like",
    { preHandler: app.requireAuth },
    async (req, reply) => {
      const { id } = req.params as { id: string };
      await app.prisma.like.upsert({
        where: { userId_trackId: { userId: req.userId!, trackId: id } },
        create: { userId: req.userId!, trackId: id },
        update: {},
      });
      return reply.code(204).send();
    }
  );

  app.delete(
    "/tracks/:id/like",
    { preHandler: app.requireAuth },
    async (req, reply) => {
      const { id } = req.params as { id: string };
      await app.prisma.like
        .delete({ where: { userId_trackId: { userId: req.userId!, trackId: id } } })
        .catch(() => null);
      return reply.code(204).send();
    }
  );

  app.get(
    "/tracks/liked",
    { preHandler: app.requireAuth },
    async (req, reply) => {
      const likes = await app.prisma.like.findMany({
        where: { userId: req.userId! },
        include: { track: true },
        orderBy: { createdAt: "desc" },
      });
      return reply.send({ tracks: likes.map((l) => l.track) });
    }
  );
}

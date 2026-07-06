import fp from "fastify-plugin";
import type { FastifyInstance, FastifyRequest, FastifyReply } from "fastify";
import { verifyToken } from "../utils/jwt.js";

declare module "fastify" {
  interface FastifyInstance {
    requireAuth: (req: FastifyRequest, reply: FastifyReply) => Promise<void>;
  }
  interface FastifyRequest {
    userId?: string;
  }
}

export default fp(async (app: FastifyInstance) => {
  app.decorate("requireAuth", async (req: FastifyRequest, reply: FastifyReply) => {
    const header = req.headers.authorization;
    if (!header?.startsWith("Bearer ")) {
      return reply.code(401).send({ error: "Missing bearer token" });
    }
    try {
      const payload = verifyToken(header.slice("Bearer ".length));
      req.userId = payload.userId;
    } catch {
      return reply.code(401).send({ error: "Invalid or expired token" });
    }
  });
});

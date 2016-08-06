package com.abecderic.labyrinth.util;

import com.abecderic.labyrinth.config.Config;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LabyrinthTeleporterPortal
{
    private static final int DESTINATION_DELAY = 60000;
    private static LabyrinthTeleporterPortal instance;
    private Map<BlockPos, BlockPos> destinationMap = new HashMap<BlockPos, BlockPos>();
    private Random r = new Random();

    public void teleportEntity(MinecraftServer server, BlockPos origin, EntityPlayerMP entity)
    {
        BlockPos destination = destinationMap.get(origin);
        if (destination == null)
        {
            int x = (int) Math.floor(entity.getPosition().getX() / 16D);
            int z = (int) Math.floor(entity.getPosition().getZ() / 16D);
            x += r.nextGaussian() * 8;
            z += r.nextGaussian() * 8;
            x = x * 16 + 8;
            z = z * 16 + 8;
            if (entity.dimension == Config.getConfig().dimId)
            {
                server.getPlayerList().transferPlayerToDimension(entity, 0, new LabyrinthTeleporter((WorldServer) server.getEntityWorld()));
                destination = new BlockPos(x, 256, z);
                destination = new BlockPos(x, entity.getEntityWorld().getTopSolidOrLiquidBlock(destination).getY(), z);
                entity.setPositionAndUpdate(destination.getX() + 0.5D, destination.getY() + 0.5D, destination.getZ() + 0.5D);
            }
            else
            {
                server.getPlayerList().transferPlayerToDimension(entity, Config.getConfig().dimId, new LabyrinthTeleporter((WorldServer) server.getEntityWorld()));
                destination = new BlockPos(x, 65, z);
                entity.setPositionAndUpdate(destination.getX() + 0.5D, destination.getY() + 0.5D, destination.getZ() + 0.5D);
            }
            destinationMap.put(origin, destination);
        }
        else if (entity.dimension == Config.getConfig().dimId)
        {
            server.getPlayerList().transferPlayerToDimension(entity, 0, new LabyrinthTeleporter((WorldServer) server.getEntityWorld()));
            entity.setPositionAndUpdate(destination.getX() + 0.5D, destination.getY() + 0.5D, destination.getZ() + 0.5D);
        }
        else
        {
            server.getPlayerList().transferPlayerToDimension(entity, Config.getConfig().dimId, new LabyrinthTeleporter((WorldServer) server.getEntityWorld()));
            entity.setPositionAndUpdate(destination.getX() + 0.5D, destination.getY() + 0.5D, destination.getZ() + 0.5D);
        }
        for (Map.Entry<BlockPos, BlockPos> entry : destinationMap.entrySet())
        {
            System.out.println("pos " + entry.getKey().getX() + "," + entry.getKey().getY() + "," + entry.getKey().getZ() + " to " + entry.getValue().getX() + "," + entry.getValue().getY() + "," + entry.getValue().getZ());
        }
    }

    public void invalidateDestination(BlockPos pos)
    {
        destinationMap.remove(pos);
    }

    public static LabyrinthTeleporterPortal getInstance()
    {
        if (instance == null)
        {
            instance = new LabyrinthTeleporterPortal();
        }
        return instance;
    }
}
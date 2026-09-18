package com.kanjpz.meowski.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * /climate - dumps exact worldgen climate values + biome + height at the
 * player's current position. Use this to calibrate terrain overrides instead
 * of eyeballing F3 - this gives the precise numbers the density functions
 * actually see.
 */
public class ClimateDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("climate")
                .requires(source -> source.hasPermission(0)) // anyone can run it, singleplayer-friendly
                .executes(ClimateDebugCommand::run));
    }

    private static int run(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Run this as a player, not from console."));
            return 0;
        }

        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();

        RandomState randomState = level.getChunkSource().randomState();
        Climate.Sampler sampler = randomState.sampler();

        Climate.TargetPoint target = sampler.sample(
                QuartPos.fromBlock(pos.getX()),
                QuartPos.fromBlock(pos.getY()),
                QuartPos.fromBlock(pos.getZ()));

        double continentalness = Climate.unquantizeCoord(target.continentalness());
        double erosion = Climate.unquantizeCoord(target.erosion());
        double temperature = Climate.unquantizeCoord(target.temperature());
        double humidity = Climate.unquantizeCoord(target.humidity());
        double weirdness = Climate.unquantizeCoord(target.weirdness());
        double depth = Climate.unquantizeCoord(target.depth());

        var biomeHolder = level.getBiome(pos);
        String biomeName = biomeHolder.unwrapKey()
                .map(k -> k.location().toString())
                .orElse("unknown");

        int surfaceY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                pos.getX(), pos.getZ());

        String msg = String.format(
                "biome=%s | feetY=%d | surfaceY=%d || C=%.4f E=%.4f T=%.4f H=%.4f W=%.4f D=%.4f",
                biomeName, pos.getY(), surfaceY,
                continentalness, erosion, temperature, humidity, weirdness, depth);

        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
        // also dump to the log so it's easy to copy/paste later without chat truncation
        org.slf4j.LoggerFactory.getLogger("meowski-climate").info(msg);
        return 1;
    }
}
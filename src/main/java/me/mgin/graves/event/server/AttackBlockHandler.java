package me.mgin.graves.event.server;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AttackBlockHandler {
    public static ActionResult handle(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        // Do not send server date
        if (!world.isClient()) return ActionResult.PASS;

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GraveBlockEntity graveEntity) {
            GameProfile graveOwner = graveEntity.getGraveOwner();

            if (graveOwner != null) {
                Date date = new Date(graveEntity.getMstime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy '('hh:mm a')'");
                String formattedDate = dateFormat.format(date);

                player.sendMessage(
                    Text.translatable("grave.died-on", graveOwner.getName(), formattedDate),
                    true
                );
            }
        }

        return ActionResult.PASS;
    }
}

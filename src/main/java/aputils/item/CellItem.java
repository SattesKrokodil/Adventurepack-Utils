package aputils.item;

import aputils.APUtilsMod;
import aputils.cell.Cell;
import aputils.util.ExileHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CellItem extends Item {

    public CellItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        if(!stack.hasTag() || !stack.getTag().contains("CellData")) {
            return new TranslatableText("item." + APUtilsMod.MODID + ".empty_cell");
        }
        CompoundTag tag = stack.getOrCreateSubTag("CellData");
        String type = "";
        String name = "";
        boolean literal = false;
        if(tag.contains("Type")) {
            type = tag.getString("Type");
        }
        if(tag.contains("Name")) {
            name = tag.getString("Name");
        }
        TranslatableText text = new TranslatableText(APUtilsMod.MODID + ".cell." + type, literal ? new LiteralText(name) : new TranslatableText(name));
        return text;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateSubTag("CellData");
        if(tag.contains("Rarity")) {
            String rarityString = tag.getString("Rarity");
            return Rarity.valueOf(rarityString.toUpperCase(Locale.ROOT));
        }
        return Rarity.COMMON;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(!stack.hasTag()) {
            return TypedActionResult.pass(stack);
        }
        CompoundTag mainTag = stack.getTag();
        if(!mainTag.contains("CellData")) {
            return TypedActionResult.pass(stack);
        }
        if(APUtilsMod.isExileLoaded) {
            int reqLevel = stack.getSubTag("CellData").getInt("MinLevel");
            int playerLevel = ExileHelper.getPlayerLevel(user);
            if(playerLevel < reqLevel) {
                return TypedActionResult.pass(stack);
            }
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)user;
            CompoundTag tag = stack.getOrCreateSubTag("CellData");
            if(tag.contains("Advancement")) {
                Identifier advancementId = new Identifier(tag.getString("Advancement"));
                Optional<Advancement> advancementOptional = APUtilsMod.advancementChecker.getAdvancement(player, advancementId);
                if(!advancementOptional.isPresent()) {
                    APUtilsMod.LOGGER.warn("Unregistered advancement ID was present on cell: \"" + advancementId.toString() + "\".");
                } else {
                    Advancement advancement = advancementOptional.get();
                    if(!APUtilsMod.advancementChecker.hasAdvancement(player, advancement)) {
                        PlayerAdvancementTracker tracker = player.getAdvancementTracker();
                        AdvancementProgress advancementProgress = tracker.getProgress(advancement);
                        if (!advancementProgress.isDone()) {
                            for(String criterion : advancementProgress.getUnobtainedCriteria()) {
                                player.getAdvancementTracker().grantCriterion(advancement, criterion);
                            }

                            if(!player.isCreative()) {
                                stack.decrement(1);
                            }

                            return stack;
                        }
                    }
                }
            }
        }

        return super.finishUsing(stack, world, user);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag tag = stack.getOrCreateSubTag("CellData");
        if(APUtilsMod.isExileLoaded) {
            if(tag.contains("MinLevel")) {
                int reqLevel = tag.getInt("MinLevel");
                int playerLevel = 0;
                if(MinecraftClient.getInstance().player != null) {
                    playerLevel = ExileHelper.getPlayerLevel(MinecraftClient.getInstance().player);
                }
                Formatting textColor = playerLevel < reqLevel ? Formatting.RED : Formatting.YELLOW;
                tooltip.add(new TranslatableText("aputils.cell.level_requirement", reqLevel).formatted(textColor));
            }
        }
        if(context.isAdvanced()) {
            if(tag.contains("Advancement")) {
                tooltip.add(new TranslatableText("aputils.cell.grants", tag.getString("Advancement")).formatted(Formatting.GRAY));
            } else {
                tooltip.add(new TranslatableText("aputils.cell.no_grant").formatted(Formatting.GRAY));
            }
        }
    }

    public static ItemStack create(Cell cell) {
        return create(cell.getAdvancement(), cell.getRarity(), cell.getCellType().name().toLowerCase(Locale.ROOT), cell.getTranslationKey(), cell.getMinLevel(), false);
    }

    public static ItemStack create(Identifier advancement, Rarity rarity, String type, String name, int minLevel, boolean isNameLiteral) {
        ItemStack itemStack = new ItemStack(APItems.CELL);
        CompoundTag tag = itemStack.getOrCreateSubTag("CellData");
        tag.putString("Advancement", advancement.toString());
        tag.putString("Rarity", rarity.name().toLowerCase(Locale.ROOT));
        tag.putString("Type", type);
        tag.putString("Name", name);
        tag.putInt("MinLevel", minLevel);
        tag.putBoolean("LiteralName", isNameLiteral);
        return itemStack;
    }
}

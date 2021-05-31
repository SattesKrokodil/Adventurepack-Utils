package aputils.command;

import aputils.cell.Cell;
import aputils.cell.CellType;
import aputils.item.CellItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Rarity;

import java.util.Collection;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CellCommand {

	private static final SuggestionProvider<ServerCommandSource> ADVANCEMENT_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		Collection<Advancement> collection = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().getAdvancements();
		return CommandSource.suggestIdentifiers(collection.stream().map(Advancement::getId), suggestionsBuilder);
	};

	private static final SuggestionProvider<ServerCommandSource> RARITY_SUGGESTION_PROVIDER = (commandContext, suggestionBuilder) -> {
		for(Rarity rarity : Rarity.values()) {
			suggestionBuilder.suggest(rarity.name().toLowerCase(Locale.ROOT));
		}
		return suggestionBuilder.buildFuture();
	};

	private static final SuggestionProvider<ServerCommandSource> CELL_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
		return CommandSource.suggestIdentifiers(Cell.REGISTRY.getIds(), suggestionsBuilder);
	};

	private static final SuggestionProvider<ServerCommandSource> TYPE_SUGGESTION_PROVIDER = (commandContext, suggestionBuilder) -> {
		for(CellType cellType : CellType.values()) {
			suggestionBuilder.suggest(cellType.name().toLowerCase(Locale.ROOT));
		}
		return suggestionBuilder.buildFuture();
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			literal("cell").requires(cs -> cs.hasPermissionLevel(2))
				.then(literal("create")
					.then(argument("advancement", IdentifierArgumentType.identifier()).suggests(ADVANCEMENT_SUGGESTION_PROVIDER)
						.then(argument("rarity", StringArgumentType.word()).suggests(RARITY_SUGGESTION_PROVIDER)
							.then(argument("type", StringArgumentType.word()).suggests(TYPE_SUGGESTION_PROVIDER)
								.then(argument("name", StringArgumentType.string())
									.then(argument("level", IntegerArgumentType.integer(0))
										.executes((command) -> {
												ItemStack item = CellItem.create(IdentifierArgumentType.getIdentifier(command, "advancement"),
													Rarity.valueOf(StringArgumentType.getString(command, "rarity").toUpperCase()),
													StringArgumentType.getString(command, "type"),
													StringArgumentType.getString(command, "name"),
													IntegerArgumentType.getInteger(command, "level"),
													true);

												Entity entity = command.getSource().getEntity();
												if(entity instanceof PlayerEntity) {
													((PlayerEntity)entity).giveItemStack(item);
												} else {
													command.getSource().getWorld().spawnEntity(new ItemEntity(command.getSource().getWorld(),
														command.getSource().getPosition().x,
														command.getSource().getPosition().y,
														command.getSource().getPosition().z,
														item));
												}
												return 1;
											}
										)
									)
								)
							)
						)
					)
				)
				.then(literal("from")
					.then(argument("cell", IdentifierArgumentType.identifier()).suggests(CELL_SUGGESTION_PROVIDER)
						.executes((command) -> {
							ItemStack item = CellItem.create(Cell.REGISTRY.get(IdentifierArgumentType.getIdentifier(command, "cell")));

							Entity entity = command.getSource().getEntity();
							if(entity instanceof PlayerEntity) {
								((PlayerEntity)entity).giveItemStack(item);
							} else {
								command.getSource().getWorld().spawnEntity(new ItemEntity(command.getSource().getWorld(),
									command.getSource().getPosition().x,
									command.getSource().getPosition().y,
									command.getSource().getPosition().z,
									item));
							}
							return 1;
						})
					)
				)
		);
	}
}

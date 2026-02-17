package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.scores.Team;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.helper.OptionsHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinAdvancementManager;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinInputUtilKey;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinPhaseType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static xyz.wagyourtail.jsmacros.client.McUtil.mc;

public class TsIdsAndEnumsGen {
    /**
     * Exposed for third party class scanners.
     */
    public static List<? extends Class<?>> screenClasses = new ArrayList<>();
    public static Supplier<Stream<Path>> modPathSupplier = () ->
            Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator)).map(Path::of);
    private final Consumer<String> consumer;
    private final ClientLevel world;
    private final LocalPlayer player;
    private final RegistryHelper registryHelper;
    private final RegistryAccess registry;

    public static void setModPathSupplier(@NotNull Supplier<Stream<Path>> supplier) {
        modPathSupplier = supplier;
    }

    /**
     * Scans the minecraft and mods jar for screen classes.
     */
    @SuppressWarnings("unused")
    public static void scanScreenClasses() {
        final String[] lastName = {"java/lang/Object"};
        final String[] lastSuper = {"java/lang/Object"};
        final boolean[] lastIsMixin = {false};
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                lastName[0] = name;
                lastSuper[0] = superName;
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (descriptor.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    lastIsMixin[0] = true;
                }
                return super.visitAnnotation(descriptor, visible);
            }
        };

        Set<String> isScreen = new HashSet<>();
        Set<String> notScreen = new HashSet<>();
        isScreen.add(Screen.class.getName().replaceAll("\\.", "/"));
        notScreen.add("java/lang/Object");
        Set<String> notDetermined = new HashSet<>();
        int flag = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        List<Path> paths = modPathSupplier.get().toList();
        JsMacros.LOGGER.info("Started scanning {} roots", paths.size());
        MutableInt count = new MutableInt();
        for (Path path : paths) {
            try (Stream<Path> stream = Files.walk(path)) {
                stream.filter(p -> p.toString().endsWith(".class"))
                        .forEachOrdered(p -> {
                            try (InputStream in = Files.newInputStream(p)) {
                                lastIsMixin[0] = false;
                                new ClassReader(in).accept(visitor, flag);
                                count.add(1);
                                if (lastIsMixin[0]) return;
                                while (true) {
                                    notDetermined.add(lastName[0]);
                                    if (notScreen.contains(lastSuper[0])) {
                                        notScreen.addAll(notDetermined);
                                        break;
                                    }
                                    if (isScreen.contains(lastSuper[0])) {
                                        isScreen.addAll(notDetermined);
                                        break;
                                    }
                                    new ClassReader(lastSuper[0]).accept(visitor, flag);
                                }
                            } catch (Throwable ignore) {}
                            notDetermined.clear();
                        });
            } catch (IOException ignore) {}
        }

        JsMacros.LOGGER.info("Scan finished on {} classes with {} results", count.intValue(), isScreen.size());
        ClassLoader loader = Minecraft.class.getClassLoader();
        screenClasses = isScreen.stream().sorted().map(name -> {
            try {
                return Class.forName(name.replaceAll("/", "."), false, loader);
            } catch (Throwable t) {
                JsMacrosClient.clientCore.profile.logError(t);
                return null;
            }
        }).filter(Objects::nonNull).filter(c -> !Modifier.isAbstract(c.getModifiers())).toList();
        JsMacros.LOGGER.info("Loaded {} classes", screenClasses.size());
    }

    public static void generate(Consumer<String> consumer) {
        new TsIdsAndEnumsGen(consumer).gen();
    }

    private TsIdsAndEnumsGen(Consumer<String> consumer) {
        this.consumer = consumer;
        ClientLevel world = mc.level;
        LocalPlayer player = mc.player;
        ClientPacketListener conn = mc.getConnection();
        if (world == null || player == null || conn == null) {
            throw new IllegalStateException("Not in a world");
        }
        this.world = world;
        this.player = player;
        registryHelper = new RegistryHelper();
        registry = conn.registryAccess();
    }

    private void gen() {
        type("Key")
                .noSort()
                .add(() -> {
                    Set<String> src = new HashSet<>(MixinInputUtilKey.getNameMap().keySet());
                    List<String> out = new ArrayList<>();
                    if (src.remove("key.mouse.left")) {
                        out.add("key.mouse.left");
                    }
                    if (src.remove("key.mouse.right")) {
                        out.add("key.mouse.right");
                    }
                    if (src.remove("key.mouse.middle")) {
                        out.add("key.mouse.middle");
                    }
                    List<Pattern> regexes = List.of(
                            Pattern.compile("^key\\.mouse\\.([0-9])$"),
                            Pattern.compile("^key\\.keyboard\\.f(0?\\d)$"),
                            Pattern.compile("^key\\.keyboard\\.f([1-9]\\d)$"),
                            Pattern.compile("^key\\.keyboard\\.([a-z0-9])$"),
                            Pattern.compile("^key\\.keyboard\\.((?:keypad|left|right)\\..+)$")
                    );
                    for (Pattern regex : regexes) {
                        List<String> group = src.stream()
                                .map(regex::matcher)
                                .filter(Matcher::matches)
                                .map(Matcher::toMatchResult)
                                .sorted(Comparator.comparing(r -> r.group(1)))
                                .map(MatchResult::group)
                                .toList();
                        src.removeAll(group);
                        out.addAll(group);
                    }
                    src.stream().sorted().forEach(out::add);
                    return out.stream();
                })
                .build();
        newLine();
        type("Bind")
                .add(Arrays.stream(mc.options.keyMappings).map(KeyMapping::getName))
                .build();
        type("Biome")
                .addRegistry(Registries.BIOME)
                .build();
        type("ItemId")
                .add(registryHelper.getItemIds())
                .build();
        type("ItemTag")
                .add(() -> StreamSupport.stream(registry.lookupOrThrow(Registries.ITEM).asHolderIdMap().spliterator(), false)
                        .flatMap(Holder::tags)
                        .map(k -> k.location().toString())
                )
                .build();
        type("SoundId")
                .addRegistry(Registries.SOUND_EVENT)
                .build();
        type("FluidId")
                .addRegistry(Registries.FLUID)
                .build();
        type("BlockId")
                .add(registryHelper.getBlockIds())
                .build();
        type("BlockTag")
                .add(registryHelper.getBlocks().stream().flatMap(b -> b.getTags().stream()))
                .build();

        newLine();
        //noinspection SpellCheckingInspection
        consumer.accept("type EntityId = keyof EntityIdToTypeMap;\n");
        consumer.accept("type EntityTypeFromId<E extends CanOmitNamespace<EntityId>> =\n");
        consumer.accept("  EntityIdToTypeMap[CompleteNamespace<E>] extends infer R ?\n");
        consumer.accept("  EntityIdToTypeMap[EntityId] extends R ? EntityHelper : R : never;\n");
        consumer.accept("type EntityIdToTypeMap = {");
        try {
            registryHelper.getEntityTypeIds().stream().distinct().sorted().forEach(id -> {
                String type = "EntityHelper";
                if (id.equals("minecraft:player")) {
                    type = "PlayerEntityHelper";
                } else {
                    try {
                        type = registryHelper.getEntity(id).getClass().getSimpleName();
                    } catch (Throwable ignore) {}
                }
                consumer.accept("\n  '");
                consumer.accept(id);
                consumer.accept("': ");
                consumer.accept(type);
            });
        } catch (Throwable t) {
            newLine();
            feedException(t, consumer);
        }
        consumer.accept("\n}\n");

        // seems like recipe id no longer exists...?
        type("RecipeId")
                .addRegistry(Registries.RECIPE)
                .build();
        type("Gamemode")
                .addEnum(GameType::values, GameType::getName)
                .build();
        type("Dimension")
                .addRegistry(Registries.DIMENSION_TYPE)
                .allowGeneric()
                .build();
        newLine();
        type("ScreenName")
                .addExtraRaw("HandledScreenName")
                .add(() -> screenClasses.stream()
                        .filter(AbstractContainerScreen.class::isAssignableFrom)
                        .map(Class::getName)
                )
                .build();
        newLine();
        type("ScreenClass")
                .add(() -> screenClasses.stream().map(Class::getSimpleName))
                .build();
//        type("ActionResult").addFields(InteractionResult.class).build();
        type("DamageSource")
                .add(() -> {
                    DamageSources sources = world.damageSources();
                    return Arrays.stream(DamageSources.class.getDeclaredFields())
                            .filter(f -> DamageSource.class.isAssignableFrom(f.getType()))
                            .map(f -> {
                                try {
                                    f.setAccessible(true);
                                    return (DamageSource) f.get(sources);
                                } catch (Throwable ignore) {}
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .map(DamageSource::getMsgId);
                })
                .build();
        type("StatusEffectId")
                .add(registryHelper.getStatusEffectIds())
                .build();
        type("PistonBehaviour")
                .addEnum(PushReaction::values)
                .build();
        type("EntityUnloadReason")
                .addEnum(Entity.RemovalReason::values)
                .build();
        type("BossBarColor")
                .addEnum(BossEvent.BossBarColor::values, BossEvent.BossBarColor::getName)
                .build();
        type("BossBarStyle")
                .addEnum(BossEvent.BossBarOverlay::values, BossEvent.BossBarOverlay::getName)
                .build();
        type("TextClickAction")
                .addEnum(ClickEvent.Action::values)
                .build();
        type("TextHoverAction")
                .addEnum(HoverEvent.Action::values, StringRepresentable::getSerializedName)
                .build();
        type("VillagerStyle")
                .addFields(VillagerType.class, ResourceKey.class, k -> k.identifier().toString())
                .build();
        type("VillagerProfession")
                .add(registryHelper.getVillagerProfessionIds())
                .build();
        newLine();
        newLine();
        type("FeatureId")
                .add(registryHelper.getFeatureIds())
                .build();
        type("PaintingId")
                .add(registryHelper.getPaintingIds())
                .build();
        type("ParticleId")
                .add(registryHelper.getParticleTypeIds())
                .build();
        type("StatTypeId")
                .add(registryHelper.getStatTypeIds())
                .build();
        type("RecipeTypeId")
                .add(registryHelper.getRecipeTypeIds())
                .build();
        type("SensorTypeId")
                .add(registryHelper.getSensorTypeIds())
                .build();
        type("PotionTypeId")
                .add(registryHelper.getPotionTypeIds())
                .build();
        type("AdvancementId")
                .add(((MixinAdvancementManager) player.connection.getAdvancements().getTree())
                        .getAdvancements().keySet().stream().map(Object::toString)
                )
                .build();
        type("ParticleTypeId")
                .add(registryHelper.getParticleTypeIds())
                .build();
        type("VillagerTypeId")
                .add(registryHelper.getVillagerTypeIds())
                .build();
        type("ActivityTypeId")
                .add(registryHelper.getActivityTypeIds())
                .build();
        type("ScreenHandlerId")
                .add(registryHelper.getScreenHandlerIds())
                .build();
        type("BlockEntityTypeId")
                .add(registryHelper.getBlockEntityTypeIds())
                .build();
        type("EntityAttributeId")
                .add(registryHelper.getEntityAttributeIds())
                .build();
        type("MemoryModuleTypeId")
                .add(registryHelper.getMemoryModuleTypeIds())
                .build();
        type("StructureFeatureId")
                .add(registryHelper.getStructureFeatureIds())
                .build();
        type("PointOfInterestTypeId")
                .add(registryHelper.getPointOfInterestTypeIds())
                .build();
        type("Locale")
                .add(mc.getLanguageManager().getLanguages().keySet())
                .build();
        type("KeyCategory")
                .add(new OptionsHelper(mc.options).getControlOptions().getCategories())
                .build();
        type("SoundCategory")
                .addEnum(SoundSource::values, SoundSource::getName)
                .build();
        type("GameEventName")
                .add(registryHelper.getGameEventNames())
                .build();
        type("EnchantmentId")
                .add(registryHelper.getEnchantmentIds())
                .build();
        type("PacketName")
                .add(new PacketByteBufferHelper().getPacketNames())
                .build();
        newLine();
        type("DyeColorName")
                .addEnum(DyeColor::values, DyeColor::getName)
                .build();
        type("DragonPhase")
                .addFields(EnderDragonPhase.class, o -> ((MixinPhaseType) o).getName())
                .build();
        type("DragonBodyPart")
                .add(() -> Arrays.stream(new EnderDragon(null, world).getSubEntities()).map(p -> p.name))
                .build();
        type("AxolotlVariant")
                .addEnum(Axolotl.Variant::values, Axolotl.Variant::getName)
                .build();
        type("FrogVariant")
                .addRegistry(Registries.FROG_VARIANT)
                .build();
        type("LlamaVariant")
                .addEnum(Llama.Variant::values, StringRepresentable::getSerializedName)
                .build();
        type("PandaGene")
                .addEnum(Panda.Gene::values, StringRepresentable::getSerializedName)
                .build();
        type("ParrotVariant")
                .addEnum(Parrot.Variant::values, StringRepresentable::getSerializedName)
                .build();
        type("RabbitVariant")
                .addEnum(Rabbit.Variant::values, StringRepresentable::getSerializedName)
                .build();
        type("TropicalVariant")
                .addEnum(TropicalFish.Pattern::values, StringRepresentable::getSerializedName)
                .build();
        type("TropicalSize")
                .addEnum(TropicalFish.Base::values)
                .build();
        type("StatusEffectCategory")
                .addEnum(MobEffectCategory::values)
                .build();
        type("RecipeBookCategory")
                .addEnum(RecipeBookType::values)
                .build();
        type("BeaconStatusEffect")
                .add(BeaconBlockEntity.BEACON_EFFECTS.stream().flatMap(List::stream).map(Holder::getRegisteredName))
                .build();
        type("FormattingColorName")
                .add(ChatFormatting.getNames(true, false))
                .build();
        type("TeamCollisionRule")
                .addEnum(Team.CollisionRule::values, StringRepresentable::getSerializedName)
                .build();
        type("TeamVisibilityRule")
                .addEnum(Team.Visibility::values, StringRepresentable::getSerializedName)
                .build();
    }

    private TypeBuilder type(String name) {
        return new TypeBuilder(name);
    }

    private void newLine() {
        consumer.accept("\n");
    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private class TypeBuilder {
        private final String name;
        // not affected by extraTypes
        private String defaultValue = "string";
        private boolean allowGeneric = false;
        private Stream<String> types = Stream.empty();
        // added to the end without sorting
        private Stream<String> extraTypes = Stream.empty();
        private boolean sort = true;
        private final List<Throwable> exceptions = new ArrayList<>();

        private TypeBuilder(String name) {
            this.name = name;
            JsMacros.LOGGER.info("Generating {}", name);
        }

        private TypeBuilder defaultTo(String value) {
            this.defaultValue = value;
            return this;
        }

        private TypeBuilder allowGeneric() {
            this.allowGeneric = true;
            return this;
        }

        private TypeBuilder noSort() {
            this.sort = false;
            return this;
        }

        private TypeBuilder add(Collection<String> types) {
            return add(types.stream());
        }

        private TypeBuilder add(Stream<String> types) {
            this.types = Stream.concat(this.types, types.map(TsIdsAndEnumsGen::wrapWithQuotes));
            return this;
        }

        private <T extends Enum<?>> TypeBuilder addEnum(Supplier<T[]> values) {
            return addEnum(values, Enum::name);
        }

        private <T extends Enum<?>> TypeBuilder addEnum(Supplier<T[]> values, Function<T, String> mapper) {
            return add(Arrays.stream(values.get()).map(mapper));
        }

        private TypeBuilder addRegistry(ResourceKey<? extends Registry<?>> key) {
            return add(() -> registry.lookupOrThrow(key).keySet().stream().map(Object::toString));
        }

        private <T> TypeBuilder addFields(Class<T> clazz, Function<T, String> mapper) {
            return addFields(clazz, clazz, mapper);
        }

        private <T> TypeBuilder addFields(Class<?> container, Class<T> ofClass, Function<T, String> mapper) {
            //noinspection unchecked
            return add(() -> Arrays.stream(container.getDeclaredFields())
                    .filter(f -> ofClass.isAssignableFrom(f.getType()))
                    .map(f -> {
                        try {
//                            f.setAccessible(true);
                            return f.get(null);
                        } catch (Throwable ignore) {}
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .map(t -> mapper.apply((T) t))
            );
        }

        private TypeBuilder add(Supplier<Stream<String>> supplier) {
            try {
                add(supplier.get());
            } catch (Throwable t) {
                this.exceptions.add(t);
            }
            return this;
        }

        private TypeBuilder addExtraRaw(String... types) {
            this.extraTypes = Stream.concat(this.extraTypes, Arrays.stream(types));
            return this;
        }

        private void build() {
            boolean multiLine = false;
            this.types = this.types.distinct();
            if (this.sort) {
                this.types = this.types.sorted();
            }
            List<String> types = new ArrayList<>(this.types.toList());
            List<String> extra = this.extraTypes.toList();
            types.remove(null);
            if (types.isEmpty()) {
                String first = this.defaultValue;
                if (!extra.isEmpty() && first.equals("string")) {
                    first = "string & {}";
                }
                if (!extra.isEmpty()) {
                    multiLine = true;
                }
                types.add(first + " // not found");
            } else if (this.allowGeneric && !types.contains("string & {}")) {
                types.add("string & {}");
            }
            types.addAll(extra);
            if (name.length() + types.stream().mapToInt(String::length).sum() + (types.size() - 1) * " | ".length() > 72) {
                multiLine = true;
            }

            newLine();
            consumer.accept("type ");
            consumer.accept(this.name);
            consumer.accept(" =");
            if (multiLine) {
                for (String type : types) {
                    consumer.accept("\n| ");
                    consumer.accept(type);
                }
            } else {
                consumer.accept(types.stream().map(s -> " " + s).collect(Collectors.joining(" |")));
            }
            newLine();
            for (Throwable t : exceptions) {
                feedException(t, consumer);
                newLine();
            }
        }
    }

    private static String wrapWithQuotes(String str) {
        //noinspection RedundantEscapeInRegexReplacement
        return "'" + str.replaceAll("\\\\", "\\\\").replaceAll("'", "\\'") + "'";
    }

    private static void feedException(Throwable exception, Consumer<String> consumer) {
        //noinspection SuspiciousRegexArgument
        consumer.accept(exception.toString().replaceAll("^", "// "));
    }
}

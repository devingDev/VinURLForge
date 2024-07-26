package com.vinurl;

import com.mojang.logging.LogUtils;
import com.vinurl.items.VinURLDiscItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VinURL.MODID)
public class VinURL
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "vinurl";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    public VinURL()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }


    /**
     *
     * Original code from VinURL:
     */
    public static final String MOD_ID = "vinurl";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path VINURLPATH = FabricLoader.getInstance().getGameDir().resolve(MOD_ID);
    public static final Identifier PLACEHOLDER_SOUND_IDENTIFIER = new Identifier(MOD_ID, "placeholder_sound");
    public static final SoundEvent PLACEHOLDER_SOUND = Registry.register(
            Registries.SOUND_EVENT,
            PLACEHOLDER_SOUND_IDENTIFIER,
            SoundEvent.of(PLACEHOLDER_SOUND_IDENTIFIER)
    );
    public static final Item CUSTOM_RECORD = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "custom_record"),
            new VinURLDiscItem(
                    17, PLACEHOLDER_SOUND, new Item.Settings().maxCount(1), 0
            )
    );

    @Override
    public void onInitialize() {
        // Register the Custom Record to the Tools Item Group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((content) -> {
            content.add(CUSTOM_RECORD);
        });

        PayloadTypeRegistry.playS2C().register(PlaySoundPayload.ID, PlaySoundPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RecordGUIPayload.ID, RecordGUIPayload.CODEC);

        // Server event handler for setting the URL on the Custom Record
        PayloadTypeRegistry.playC2S().register(SetURLPayload.ID, SetURLPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SetURLPayload.ID, (payload, context) -> {
            PlayerEntity player = context.player();
            ItemStack currentItem = player.getStackInHand(player.getActiveHand());

            if (currentItem.getItem() != CUSTOM_RECORD) {
                return;
            }

            String urlName = payload.urlName();

            try {
                new URI(urlName).toURL();

            } catch (Exception e) {
                player.sendMessage(Text.literal("Song URL is invalid!"));
                return;
            }

            if (urlName.length() >= 400) {
                player.sendMessage(Text.literal("Song URL is too long!"));
                return;
            }

            player.playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, SoundCategory.BLOCKS, 1.0f, 1.0f);
            NbtCompound currentNbt = new NbtCompound();
            currentNbt.putString("music_url", urlName);
            currentItem.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(currentNbt));
        });
    }

    public record PlaySoundPayload(BlockPos blockPos, String urlName) implements CustomPayload {
        public static final CustomPayload.Id<PlaySoundPayload> ID = CustomPayload.id("vinurl:play_sound");
        public static final PacketCodec<RegistryByteBuf, PlaySoundPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, PlaySoundPayload::blockPos, PacketCodecs.STRING, PlaySoundPayload::urlName, PlaySoundPayload::new);

        @Override
        public Id<PlaySoundPayload> getId() {
            return ID;
        }
    }

    public record SetURLPayload(String urlName) implements CustomPayload {
        public static final CustomPayload.Id<SetURLPayload> ID = CustomPayload.id("vinurl:record_set_url");
        public static final PacketCodec<RegistryByteBuf, SetURLPayload> CODEC = PacketCodecs.STRING.xmap(SetURLPayload::new, SetURLPayload::urlName).cast();

        @Override
        public Id<SetURLPayload> getId() {
            return ID;
        }
    }


    public record RecordGUIPayload(String urlName) implements CustomPayload {
        public static final CustomPayload.Id<RecordGUIPayload> ID = CustomPayload.id("vinurl:record_gui");
        public static final PacketCodec<RegistryByteBuf, RecordGUIPayload> CODEC = PacketCodecs.STRING.xmap(RecordGUIPayload::new, RecordGUIPayload::urlName).cast();


        @Override
        public Id<RecordGUIPayload> getId() {
            return ID;
        }
    }

}

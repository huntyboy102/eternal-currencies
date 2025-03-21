package com.mceternal.eternalcurrencies;

import com.mceternal.eternalcurrencies.command.EternalCurrenciesCommands;
import com.mceternal.eternalcurrencies.data.CurrencyDataHolder;
import com.mceternal.eternalcurrencies.data.EternalCurrenciesRegistries;
import com.mceternal.eternalcurrencies.data.CurrencyData;
import com.mceternal.eternalcurrencies.data.CurrencyDataManager;
import com.mceternal.eternalcurrencies.integration.ftbquests.QuestsIntegration;
import com.mceternal.eternalcurrencies.network.PacketHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EternalCurrencies.MODID)
public class EternalCurrencies {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "eternalcurrencies";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final ResourceLocation CURRENCIES_CAP_NAME = ResourceLocation.fromNamespaceAndPath(MODID, "currencies");

    public static CurrencyDataHolder CURRENCY_DATA_HOLDER;

    public static boolean FTBQ_LOADED = ModList.get().isLoaded("ftbquests");


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .build());

    public EternalCurrencies(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        if(FTBQ_LOADED)
            QuestsIntegration.init();

        DistExecutor.runWhenOn(Dist.CLIENT, EternalCurrenciesClient::new);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void preInit(FMLCommonSetupEvent event) {

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.register();
            EternalCurrencies.LOGGER.info("registered Packets");
        });
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    public static CurrencyDataHolder getCurrencyHolder() {
        if(CURRENCY_DATA_HOLDER == null)
            throw new IllegalStateException("CurrencyDataHolder hasn't been instantiated yet. Wait until Resources are loaded!");
        return CURRENCY_DATA_HOLDER;
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class ServerModEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            EternalCurrenciesCommands.register(event.getDispatcher(), event.getBuildContext());
        }

        //@SubscribeEvent
        public static void registerCurrencies(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(EternalCurrenciesRegistries.CURRENCY_DATA, CurrencyData.CODEC);
        }

        @SubscribeEvent
        public static void onResourceReload(AddReloadListenerEvent event) {
            CURRENCY_DATA_HOLDER = new CurrencyDataManager(event.getRegistryAccess());
            event.addListener((CurrencyDataManager) CURRENCY_DATA_HOLDER);
        }
    }
}

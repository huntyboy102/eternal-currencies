package com.mceternal.eternalcurrencies;

import com.mceternal.eternalcurrencies.command.EternalCurrenciesCommands;
import com.mceternal.eternalcurrencies.data.*;
import com.mceternal.eternalcurrencies.data.shop.entry.ECShopEntryTypes;
import com.mceternal.eternalcurrencies.data.shop.requirement.ECShopRequirementTypes;
import com.mceternal.eternalcurrencies.integration.ftbquests.QuestsIntegration;
import com.mceternal.eternalcurrencies.item.EternalCurrenciesItems;
import com.mceternal.eternalcurrencies.item.ItemCurrencyCheque;
import com.mceternal.eternalcurrencies.network.PacketHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraftforge.registries.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EternalCurrencies.MODID)
public class EternalCurrencies {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "eternalcurrencies";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    // Resource location for the currency data capability
    public static final ResourceLocation CURRENCIES_CAP_NAME = resource("currencies");

    // Resource location for the currency coins item
    public static final ResourceLocation CURRENCY_COINS = resource("coins");

    // Check if FTB Quests mod is loaded
    public static boolean FTBQ_LOADED = ModList.get().isLoaded("ftbquests");

    // Deferred registry for Creative Mode Tabs
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Register the main creative tab
    public static final RegistryObject<CreativeModeTab> EC_CREATIVE_TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)  // Place before Combat tab
                    .icon(() -> new ItemStack(EternalCurrenciesItems.CHEQUE.get()))  // Icon for the tab
                    .title(Component.translatable("mod.eternalcurrencies.name"))  // Title of the tab in translation files
                    .build());

    // Utility method to create ResourceLocation objects
    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // Constructor for EternalCurrencies mod class
    public EternalCurrencies(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register items
        EternalCurrenciesItems.register(modEventBus);

        // Register common setup method
        modEventBus.addListener(this::commonSetup);

        // Add items to creative tab
        modEventBus.addListener(this::addCreative);

        // Register currencies and registries
        modEventBus.addListener(EternalCurrenciesRegistries::registerCurrencies);
        modEventBus.addListener(EternalCurrenciesRegistries::addRegistries);

        // Register shop entry types and requirements
        ECShopEntryTypes.register(modEventBus);
        ECShopRequirementTypes.register(modEventBus);

        // Register events for server and other game events
        MinecraftForge.EVENT_BUS.register(this);

        // Initialize FTB Quests integration if loaded
        if (FTBQ_LOADED)
            QuestsIntegration.init();

        // Register creative tabs
        CREATIVE_MODE_TABS.register(modEventBus);

        // Run client-specific setup code on the client side
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EternalCurrenciesClient::new);
    }

    // Method to perform common setup tasks
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.register();
            EternalCurrencies.LOGGER.info("registered Packets");
        });
    }

    // Method to add items to the creative tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == EC_CREATIVE_TAB.get()) {  // Check if it's our custom tab
            Optional<HolderLookup.RegistryLookup<CurrencyData>> currencyLookup = event.getParameters().holders().lookup(EternalCurrenciesRegistries.KEY_CURRENCIES);
            currencyLookup.ifPresent(registryLookup -> {
                ItemCurrencyCheque.getVariantForEachCurrency(registryLookup.listElementIds().map(ResourceKey::location))
                        .forEach(variant -> event.accept(variant, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            });

            // Add debit card to the creative tab
            event.accept(EternalCurrenciesItems.DEBIT_CARD);
        }
    }

    // Inner class for client-specific events
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        // Method to handle client setup event
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        // Method to register key bindings for the client side
        @SubscribeEvent
        public static void onRegisterKeybinds(RegisterKeyMappingsEvent event) {
            event.register(EternalCurrenciesClient.KEY_OPEN_SHOP);  // Register key binding for opening the shop
        }
    }

    // Inner class for server-specific events
    @Mod.EventBusSubscriber(modid = MODID)
    public static class ServerModEvents {
        // Method to handle command registration on the server side
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            EternalCurrenciesCommands.register(event.getDispatcher(), event.getBuildContext());

            // Uncomment the following line to log shop entry type registry entries
            // EternalCurrenciesRegistries.SHOP_ENTRY_TYPES.get().forEach(codec ->
            //         LOGGER.info("ShopEntry Type Registry entry: {}", codec.toString()));
        }
    }
}

package com.mceternal.eternalcurrencies.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CurrenciesCapabilities {

    public static final Capability<ICurrencies> PLAYER_CURRENCY = CapabilityManager.get(new CapabilityToken<>() {});
}

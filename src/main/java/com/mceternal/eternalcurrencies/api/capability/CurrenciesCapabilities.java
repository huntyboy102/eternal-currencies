package com.mceternal.eternalcurrencies.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CurrenciesCapabilities {

    public static final Capability<ICurrencies> CURRENCY_BEARER = CapabilityManager.get(new CapabilityToken<>() {});
}

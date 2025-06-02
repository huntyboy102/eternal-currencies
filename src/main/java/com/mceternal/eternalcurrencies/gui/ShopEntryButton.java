package com.mceternal.eternalcurrencies.gui;

import com.mceternal.eternalcurrencies.api.shop.ShopEntry;
import com.mceternal.eternalcurrencies.network.C2SBuyShopEntryPacket;
import com.mceternal.eternalcurrencies.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ShopEntryButton extends Button {

    public final ResourceLocation parentCategory;
    public final ShopEntry content;

    protected ShopEntryButton(int guiWidth, int index, ResourceLocation parentCategory, ShopEntry content) {
        super(guiWidth, 5 + (15 * index), 80, 15, CommonComponents.EMPTY, pButton -> {}, DEFAULT_NARRATION);
        this.parentCategory = parentCategory;
        this.content = content;
        this.setTooltip(Tooltip.create(content.getName()));
    }

    public void onPress() {
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Clicked a Shop Entry!"), false);
        ShopEntry entry = this.content;
        PacketHandler.sendToServer(new C2SBuyShopEntryPacket(this.parentCategory, entry.id));
    }
}

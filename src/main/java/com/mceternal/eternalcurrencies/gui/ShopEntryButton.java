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

    // The parent category associated with this shop entry button
    public final ResourceLocation parentCategory;

    // The shop entry content associated with this button
    public final ShopEntry content;

    // Constructor for the ShopEntryButton
    protected ShopEntryButton(int guiWidth, int index, ResourceLocation parentCategory, ShopEntry content) {
        super(guiWidth, 5 + (15 * index), 80, 15, CommonComponents.EMPTY, pButton -> {}, DEFAULT_NARRATION);
        this.parentCategory = parentCategory;
        this.content = content;
        // Set the tooltip for the button to display the shop entry's name
        this.setTooltip(Tooltip.create(content.getName()));
    }

    // Method called when the button is pressed
    public void onPress() {
        // Display a client-side message indicating that a shop entry was clicked
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Clicked a Shop Entry!"), false);

        // Get the shop entry associated with this button
        ShopEntry entry = this.content;

        // Send a packet to the server to request purchasing the shop entry
        PacketHandler.sendToServer(new C2SBuyShopEntryPacket(this.parentCategory, entry.id));
    }
}

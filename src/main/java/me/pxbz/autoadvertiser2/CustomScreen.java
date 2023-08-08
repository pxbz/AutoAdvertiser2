package me.pxbz.autoadvertiser2;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CustomScreen extends Screen {
    private final TextFieldWidget textField;
    private static String enteredText = null;

    public CustomScreen() {
        super(Text.of(""));
        textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
                MinecraftClient.getInstance().getWindow().getWidth() / 2 - 100,
                MinecraftClient.getInstance().getWindow().getHeight() / 2 - 15,
                200,
                30,
                Text.of(""));
        textField.setText(getEnteredText());
    }

    @Override
    public void init() {
        addDrawableChild(this.textField);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int windowWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        textField.setX(windowWidth / 2 - textField.getWidth() / 2);
        textField.setY(windowHeight / 2 - textField.getHeight() / 2);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            enteredText = textField.getText();
            MinecraftClient.getInstance().setScreen(null);
            return false;
        }
        return textField.keyPressed(keyCode, scanCode, modifiers);
    }

    public static String getEnteredText() {
        return enteredText;
    }
}
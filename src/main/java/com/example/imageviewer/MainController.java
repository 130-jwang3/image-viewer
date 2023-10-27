package com.example.imageviewer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    private Text title;

    @FXML
    private Button exitBtn;

    @FXML
    private Pane imagePane;

    @FXML
    private Canvas imageCanvas;

    @FXML
    private Pane transformPane;

    @FXML
    private Canvas transformCanvas;

    @FXML
    private Button importBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button nextSceneButton;


    @FXML
    private Text brightness;

    @FXML
    private Text brightnessValue;


    private Image originalImage = null;

    private int scene = 0;

    private void sceneAction() {
        if (scene == 0) {
            title.setTextAlignment(TextAlignment.CENTER);
            title.setText("Image Viewer");
            setBrightnessVisible(false);
            processTifFile(null);
        } else if (scene == 1) {
            processTifFile(null);
            title.setTextAlignment(TextAlignment.CENTER);
            title.setText("Brightness Viewer");
            brightnessValue.setText("50%");
            setBrightnessVisible(true);
            applyBrightnessToCanvas(0.5);
        } else if (scene == 2) {
            title.setTextAlignment(TextAlignment.CENTER);
            title.setText("Ordered Dithered Image Viewer");
            processTifFile(null);
            orderDitherTifFile();
            setBrightnessVisible(false);
        } else if (scene == 3) {
            title.setTextAlignment(TextAlignment.CENTER);
            title.setText("Auto Level Image Viewer");
            processTifFile(null);
            autoLevelTifFile();
            setBrightnessVisible(false);
        }
    }

    private void processTifFile(File file) {
        try {
            BufferedImage image=null;

            if (file != null) {
                image = ImageIO.read(file);
                originalImage = SwingFXUtils.toFXImage(image, null);
            } else if (originalImage != null){
                image = SwingFXUtils.fromFXImage(originalImage, null);;
            } else {
                return;
            }
            // Create a GraphicsContext to draw on the Canvas
            GraphicsContext gc = imageCanvas.getGraphicsContext2D();
            GraphicsContext transformedGc = transformCanvas.getGraphicsContext2D();

            // Clear the Canvas
            gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
            transformedGc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());

            int canvasWidth = (int) imageCanvas.getWidth();
            int canvasHeight = (int) imageCanvas.getHeight();

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            // Calculate the position to center the image
            int x = (canvasWidth - imageWidth) / 2;
            int y = (canvasHeight - imageHeight) / 2;

            // Iterate through the image pixels and draw them
            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageHeight; j++) {
                    int rgb = image.getRGB(i, j);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Set the pixel color on the Canvas
                    gc.getPixelWriter().setColor(x + i, y + j, Color.rgb(red, green, blue));
                }
            }

            int transformCanvasWidth = (int) imageCanvas.getWidth();
            int transformCanvasHeight = (int) imageCanvas.getHeight();

            int transformedX = (transformCanvasWidth - imageWidth) / 2;
            int transformedY = (transformCanvasHeight - imageHeight) / 2;

            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageHeight; j++) {
                    int rgb = image.getRGB(i, j);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    int gray = (red + green + blue) / 3;
                    // Set the pixel color on the Canvas
                    transformedGc.getPixelWriter().setColor(transformedX + i, transformedY + j, Color.grayRgb(gray));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearCanvas(Canvas canvas) {
        // Get the canvas graphics context
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Clear the canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void setBrightnessVisible(Boolean visible) {
        brightness.setVisible(visible);
        brightnessValue.setVisible(visible);
    }

    private void applyBrightnessToCanvas(Double brightness) {
        BufferedImage image=null;
        if (originalImage != null){
            image = SwingFXUtils.fromFXImage(originalImage, null);;
        } else {
            return;
        }
        // Create a GraphicsContext to draw on the Canvas
        GraphicsContext gc = imageCanvas.getGraphicsContext2D();
        GraphicsContext transformedGc = transformCanvas.getGraphicsContext2D();

        // Clear the Canvas
        gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
        transformedGc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());

        int canvasWidth = (int) imageCanvas.getWidth();
        int canvasHeight = (int) imageCanvas.getHeight();

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int brightnessMultiplier = (int) (255 * brightness);

        // Calculate the position to center the image
        int x = (canvasWidth - imageWidth) / 2;
        int y = (canvasHeight - imageHeight) / 2;

        // Iterate through the image pixels and draw them
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                int rgb = image.getRGB(i, j);
                int red = (int)(((rgb >> 16) & 0xFF) * brightness);
                int green = (int)(((rgb >> 8) & 0xFF) * brightness);
                int blue = (int)((rgb & 0xFF) * brightness);

                // Ensure values are in the valid 0-255 range
                red = Math.min(Math.max(0, red), 255);
                green = Math.min(Math.max(0, green), 255);
                blue = Math.min(Math.max(0, blue), 255);

                // Set the pixel color on the Canvas
                gc.getPixelWriter().setColor(x + i, y + j, Color.rgb(red, green, blue));
            }
        }

        int transformCanvasWidth = (int) imageCanvas.getWidth();
        int transformCanvasHeight = (int) imageCanvas.getHeight();

        int transformedX = (transformCanvasWidth - imageWidth) / 2;
        int transformedY = (transformCanvasHeight - imageHeight) / 2;

        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                int rgb = image.getRGB(i, j);
                int red = (int)(((rgb >> 16) & 0xFF) * brightness);
                int green = (int)(((rgb >> 8) & 0xFF) * brightness);
                int blue = (int)((rgb & 0xFF) * brightness);

                // Ensure values are in the valid 0-255 range
                red = Math.min(Math.max(0, red), 255);
                green = Math.min(Math.max(0, green), 255);
                blue = Math.min(Math.max(0, blue), 255);

                int gray = (red + green + blue) / 3;
                // Set the pixel color on the Canvas
                transformedGc.getPixelWriter().setColor(transformedX + i, transformedY + j, Color.grayRgb(gray));
            }
        }

    }

    private void orderDitherTifFile(){
        BufferedImage image=null;
        if (originalImage != null){
            image = SwingFXUtils.fromFXImage(originalImage, null);;
        } else {
            return;
        }
        // Create a GraphicsContext to draw on the Canvas
        GraphicsContext transformedGc = transformCanvas.getGraphicsContext2D();

        // Clear the Canvas
        transformedGc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int transformCanvasWidth = (int) imageCanvas.getWidth();
        int transformCanvasHeight = (int) imageCanvas.getHeight();

        int transformedX = (transformCanvasWidth - imageWidth) / 2;
        int transformedY = (transformCanvasHeight - imageHeight) / 2;

        int[][] ditherMatrix = {
                {  0,  32,   8,  40,   2,  34,  10,  42 },
                { 48,  16,  56,  24,  50,  18,  58,  26 },
                { 12,  44,   4,  36,  14,  46,   6,  38 },
                { 60,  28,  52,  20,  62,  30,  54,  22 },
                {  3,  35,  11,  43,   1,  33,   9,  41 },
                { 51,  19,  59,  27,  49,  17,  57,  25 },
                { 15,  47,   7,  39,  13,  45,   5,  37 },
                { 63,  31,  55,  23,  61,  29,  53,  21 }
        };

        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                int rgb = image.getRGB(i, j);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Ensure values are in the valid 0-255 range
                red = Math.min(Math.max(0, red), 255);
                green = Math.min(Math.max(0, green), 255);
                blue = Math.min(Math.max(0, blue), 255);

                // Calculate the grayscale value
                int gray = (red + green + blue) / 3;

                // Scale the grayscale value to 0-16 range
                int scaledGray = (gray * 64) / 255;

                // Apply dithering logic
                if (scaledGray < ditherMatrix[j % 4][i % 4]) {
                    // Set the pixel color to white
                    transformedGc.getPixelWriter().setColor(transformedX + i, transformedY + j, Color.BLACK);
                } else {
                    // Set the pixel color to black
                    transformedGc.getPixelWriter().setColor(transformedX + i, transformedY + j, Color.WHITE);
                }
            }
        }
    }

    private void autoLevelTifFile(){
        BufferedImage image=null;
        if (originalImage != null){
            image = SwingFXUtils.fromFXImage(originalImage, null);;
        } else {
            return;
        }
        // Create a BufferedImage from the original image
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);

        // Get the GraphicsContext for the Canvas
        GraphicsContext transformedGc = transformCanvas.getGraphicsContext2D();

        // Calculate the Canvas dimensions
        double canvasWidth = transformCanvas.getWidth();
        double canvasHeight = transformCanvas.getHeight();

        // Clear the Canvas
        transformedGc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Get the image dimensions
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int transformCanvasWidth = (int) imageCanvas.getWidth();
        int transformCanvasHeight = (int) imageCanvas.getHeight();

        // Calculate the position to center the image on the Canvas
        int transformedX = (transformCanvasWidth - imageWidth) / 2;
        int transformedY = (transformCanvasHeight - imageHeight) / 2;

        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;
        int[] redHistogram = new int[256];
        int[] greenHistogram = new int[256];
        int[] blueHistogram = new int[256];

        // Calculate histograms for the Red, Green, and Blue channels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                redHistogram[red]++;
                greenHistogram[green]++;
                blueHistogram[blue]++;
            }
        }

        // Calculate cumulative distribution function (CDF) for each channel
        int[] redCdf = new int[256];
        int[] greenCdf = new int[256];
        int[] blueCdf = new int[256];

        redCdf[0] = redHistogram[0];
        greenCdf[0] = greenHistogram[0];
        blueCdf[0] = blueHistogram[0];

        for (int i = 1; i < 256; i++) {
            redCdf[i] = redCdf[i - 1] + redHistogram[i];
            greenCdf[i] = greenCdf[i - 1] + greenHistogram[i];
            blueCdf[i] = blueCdf[i - 1] + blueHistogram[i];
        }

        // Find the new black and white points for each channel
        int redMin = 0;
        int greenMin = 0;
        int blueMin = 0;

        while (redCdf[redMin] == 0) {
            redMin++;
        }

        while (greenCdf[greenMin] == 0) {
            greenMin++;
        }

        while (blueCdf[blueMin] == 0) {
            blueMin++;
        }

        int redMax = 255;
        int greenMax = 255;
        int blueMax = 255;

        while (redCdf[redMax] == totalPixels) {
            redMax--;
        }

        while (greenCdf[greenMax] == totalPixels) {
            greenMax--;
        }

        while (blueCdf[blueMax] == totalPixels) {
            blueMax--;
        }

        // Apply Auto Level adjustment to each channel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int newRed = (int) (255.0 * (red - redMin) / (redMax - redMin));
                int newGreen = (int) (255.0 * (green - greenMin) / (greenMax - greenMin));
                int newBlue = (int) (255.0 * (blue - blueMin) / (blueMax - blueMin));

                // Ensure values are in the valid 0-255 range
                newRed = Math.min(Math.max(0, newRed), 255);
                newGreen = Math.min(Math.max(0, newGreen), 255);
                newBlue = Math.min(Math.max(0, newBlue), 255);

                transformedGc.getPixelWriter().setColor(transformedX + x, transformedY + y, Color.rgb(newRed, newGreen, newBlue));
            }
        }

    }

    public void autoLevel(BufferedImage image) {

    }


    public void importButtonAction(ActionEvent event) {
        FileChooser fc = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TIF Files", "*.tif");
        fc.getExtensionFilters().add(extensionFilter);
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            processTifFile(selectedFile);
            sceneAction();
        }
    }

    public void nextSceneButtonAction(ActionEvent event) {
        // Toggle visibility of elements in BrightnessController
        scene++;
        if (scene == 4) {
            scene = 0;
        }
        sceneAction();
    }

    public void resetButtonAction(ActionEvent event) {
        // Clear the left and right channel canvases
        clearCanvas(imageCanvas);
        clearCanvas(transformCanvas);
        originalImage = null;
    }

    public void exitButtonAction(ActionEvent event) {
        Platform.exit();
    }

}
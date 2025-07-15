
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ImageDisplay {

    JFrame frame;
    JLabel lbIm1;
    BufferedImage imgObj;
    BufferedImage imgRef;
    int width = 640; // default image width and height
    int height = 480;

    /**
     * Read Image RGB
     * Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */

    private void readImageRGBOrg(int width, int height, String imgPath, BufferedImage img)
    {
        try
        {
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for(int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind + height * width];
                    byte b = bytes[ind + height * width * 2];
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    img.setRGB(x,y,pix);

                    ind++;
                }

            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }






    }
    public int[][] numIslands(char[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int count = 0;
        List<IslandInfo> islandInfoList = new ArrayList<>();

        // Define directions to explore (up, down, left, right, and diagonals)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    IslandInfo info = new IslandInfo();
                    dfs(grid, i, j, m, n, directions, info);

                    // Check if the island has at least 3 '1's
                    if (info.numOnes >= 1400) {
                        count++;
                        islandInfoList.add(info);
                    }
                }
            }
        }
        int numsi=islandInfoList.size();
//        System.out.println("numsi"+ numsi);
        // Print the information for each valid island
        int[][] locals= new int[numsi][4];
        for (int i = 0; i < islandInfoList.size(); i++) {
            IslandInfo info = islandInfoList.get(i);
            locals[i][0]=info.minX;
            locals[i][1]=info.maxX;
            locals[i][2]=info.minY;
            locals[i][3]=info.maxY;



            System.out.println("Island " + (i + 1) + ":");
            System.out.println("Number of '1's: " + info.numOnes);
            System.out.println("Min X: " + info.minX + ", Max X: " + info.maxX);
            System.out.println("Min Y: " + info.minY + ", Max Y: " + info.maxY);
        }

        return locals;
    }




    private void dfs(char[][] grid, int startX, int startY, int m, int n, int[][] directions, IslandInfo info) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});

        while (!stack.isEmpty()) {
            int[] coordinates = stack.pop();
            int x = coordinates[0];
            int y = coordinates[1];

            if (x < 0 || x >= m || y < 0 || y >= n || grid[x][y] != '1') {
                continue;
            }

            grid[x][y] = '0'; // Mark as visited
            info.numOnes++;   // Increment the count of '1's
            info.minX = Math.min(info.minX, x);
            info.maxX = Math.max(info.maxX, x);
            info.minY = Math.min(info.minY, y);
            info.maxY = Math.max(info.maxY, y);

            // Explore all eight possible directions
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                stack.push(new int[]{newX, newY});
            }
        }
    }

    private void readImageRGBREF(int width, int height, String imgPath, BufferedImage img,int[][]hists)
    {
        byte[] bytes = new byte[  width*height*3];
//        int[] HHistogram= new int[361];
//        int[] SHistogram=new int[101];
//        int[] VHistogram=new int[101];

        try
        {
            int frameLength = width*height*3;


            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;


            raf.read(bytes);


            char[][] MaskOfIsland= new char [width][height];

            int ind = 0;
            for(int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind + height * width];
                    byte b = bytes[ind + height * width * 2];
                    int redValue = r & 0xff;
                    int greenValue = g & 0xff;
                    int blueValue = b & 0xff;

                    double[] hsv = new double[3];
                    double max = Math.max(Math.max(redValue, greenValue), blueValue);
                    double min = Math.min(Math.min(redValue, greenValue), blueValue);
                    double hue = 0;
                    if (max == redValue) {
                        hue = ((greenValue - blueValue) / (max - min) + 6) % 6;
                    } else if (max == greenValue) {
                        hue = (blueValue - redValue) / (max - min) + 2;
                    } else {
                        hue = (redValue - greenValue) / (max - min) + 4;
                    }
                    hue *= 60;
                    int hue_int=(int)Math.round(hue);
                    double saturation = (max == 0) ? 0 : ((max - min) / max) * 100;
                    double value = (max * 100)/255;
                    int saturation_int= (int)Math.round(saturation);
                    int value_int= (int)Math.round(value);
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    int percent_of_hue=hists[3][0];
//                    int threshold_hue= ((hists[0][0])/percent_of_hue)*100 ;
//                    img.setRGB(x,y,pix);


                    for (int i=0;i<361; i++){
//                        img.setRGB(x,y,pix);
                        float threshold_hue= (float) ((hists[0][i])*100)/percent_of_hue;
//                        System.out.println(threshold_hue);

                        if (threshold_hue<0.09 & threshold_hue>0.01 & hue_int==i & saturation_int>100){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=0.09 & threshold_hue<0.3 & hue_int==i & saturation_int>90){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=0.4 & threshold_hue<0.7  & hue_int==i & saturation_int>60){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=0.7 & threshold_hue<1.0  & hue_int==i & saturation_int>30){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=1.0 & threshold_hue<1.4  & hue_int==i & saturation_int>20){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=1.4 & threshold_hue<2.0  & hue_int==i & saturation_int>15){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }

                        if (threshold_hue>=2.0 & threshold_hue<2.4  & hue_int==i & saturation_int> 10){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=2.4 & threshold_hue<3  & hue_int==i & saturation_int>10){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=3 & threshold_hue<5 & hue_int==i & saturation_int>=10){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);


                        }

                        if (threshold_hue>=5 & threshold_hue<6 & hue_int==i & saturation_int>=55){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>6 & threshold_hue<7 & hue_int==i & saturation_int>=45){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }

                        if (threshold_hue>=7 & threshold_hue<15 & hue_int==i & saturation_int>=55){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }
                        if (threshold_hue>=15 & hue_int==i & saturation_int>=65){

                            MaskOfIsland[x][y] = '1';
//                            img.setRGB(x,y,pix);
//                            img.setRGB(x,y,pix);
//                            System.out.println(threshold_hue);

                        }


                        else{
//                            MaskOfIsland[x][y] = '0';
//                            System.out.println(0);
//                            img.setRGB(x,y,pix);

                        }

                    }




                    ind++;
                }

            }

            /////////////



            ////////////dskmfsklfmsfksamfklsamfsaklfsd;lcv
            int[][] numIslands = numIslands(MaskOfIsland);
//            System.out.println("Number of Islands: " + numIslands[0][1]);
            for (int i=0; i<numIslands.length;i++){
                int r_r = 0;
                int g_r = 255;
                int b_r = 0;
                int thickness = 4;

                int pix_green = 0xff000000 | ((r_r & 0xff) << 16) | ((g_r & 0xff) << 8) | (b_r & 0xff);
                for (int x = numIslands[i][0]; x <= numIslands[i][1]; x++) {
                    for (int t = 0; t < thickness; t++) {
                        if (x + t < width) {
                            img.setRGB(x + t, numIslands[i][2], pix_green);
                            img.setRGB(x + t, numIslands[i][3], pix_green);
                        }
                    }
                }

                for (int y = numIslands[i][2]; y <= numIslands[i][3]; y++) {
                    for (int t = 0; t < thickness; t++) {
                        if (y + t < height) {
                            img.setRGB(numIslands[i][0], y + t, pix_green);
                            img.setRGB(numIslands[i][1], y + t, pix_green);
                        }
                    }
                }

            }










            }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    private int[][] readImageRGB(int width, int height, String imgPath, BufferedImage img)
    {
        byte[] bytes = new byte[  width*height*3];
        int[] HHistogram= new int[361];
        int[] percentage= new int[1];
        int[] SHistogram=new int[101];
        int[] VHistogram=new int[101];

        try
        {
            int frameLength = width*height*3;


            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;


            raf.read(bytes);

            // Calculate the histograms
            int[] ColorR= new int[height*width];
            int[] ColorG= new int[height*width];
            int[] ColorB= new int[height*width];

            int ind = 0;
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {

                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];
                    int redValue = r & 0xff;
                    int greenValue = g & 0xff;
                    int blueValue = b & 0xff;
                    ColorR[ind]=redValue;
                    ColorG[ind]=greenValue;
                    ColorB[ind]=blueValue;

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
//                    img.setRGB(x,y,pix);


                    ind++;
                }


            }
            int indx=0;
            int mask_counter=0;
            int[] MaskOfObject= new int [width*height];
            for(int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (ColorG[indx] == 255) {
                        MaskOfObject[indx] = 0;

                    }
                    else {
                        MaskOfObject[indx] = 1;
                        mask_counter++;
                    }
                    indx++;

                }



            }
            percentage[0]=mask_counter;

            ind = 0;
            for(int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind + height * width];
                    byte b = bytes[ind + height * width * 2];
                    int redValue = r & 0xff;
                    int greenValue = g & 0xff;
                    int blueValue = b & 0xff;

                    double[] hsv = new double[3];
                    double max = Math.max(Math.max(redValue, greenValue), blueValue);
                    double min = Math.min(Math.min(redValue, greenValue), blueValue);
                    double hue = 0;
                    if (max == redValue) {
                        hue = ((greenValue - blueValue) / (max - min) + 6) % 6;
                    } else if (max == greenValue) {
                        hue = (blueValue - redValue) / (max - min) + 2;
                    } else {
                        hue = (redValue - greenValue) / (max - min) + 4;
                    }
                    hue *= 60;
                    int hue_int=(int)Math.round(hue);
                    double saturation = (max == 0) ? 0 : ((max - min) / max) * 100;
                    double value = (max * 100)/255;
                    int saturation_int= (int)Math.round(saturation);
                    int value_int= (int)Math.round(value);
                    if (MaskOfObject[ind] == 1) {
//                        redHistogram[redValue]++;
//                        greenHistogram[greenValue]++;
//                        blueHistogram[blueValue]++;
                        HHistogram[hue_int]++;
                        SHistogram[saturation_int]++;
                        VHistogram[value_int]++;
                    }
//                    System.out.println("is equivalent to HSV(" + hue_int + ", " + saturation_int + ", " + value_int + ")");

                    ind++;
                }

            }
//            String imgPath2 = "outputh58.txt";
//            FileWriter writer = new FileWriter(imgPath2);
//            for (int i = 0; i < HHistogram.length; i++) {
//                writer.write(Integer.toString(HHistogram[i]));
//                // Add a newline character if it's not the last element
//                if (i < HHistogram.length - 1) {
//                    writer.write(System.lineSeparator());
//                }
//            }
//
//            String imgPath3= "outputV58.txt";
//            FileWriter writer1 = new FileWriter(imgPath3);
//            for (int i = 0; i < VHistogram.length; i++) {
//                writer1.write(Integer.toString(VHistogram[i]));
//                // Add a newline character if it's not the last element
//                if (i < VHistogram.length - 1) {
//                    writer1.write(System.lineSeparator());
//                }
//            }
//            String imgPath4= "outputS58.txt";
//            FileWriter writer2 = new FileWriter(imgPath4);
//            for (int i = 0; i < SHistogram.length; i++) {
//                writer2.write(Integer.toString(SHistogram[i]));
//                // Add a newline character if it's not the last element
//                if (i < SHistogram.length - 1) {
//                    writer2.write(System.lineSeparator());
//                }
//            }
//            System.out.println("Array data has been saved to " + imgPath2);

        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        int[][] arrayOfArrays = new int[][]{HHistogram, SHistogram, SHistogram, percentage};
        return arrayOfArrays;
    }

    public void showIms(String[] args){

        // Read a parameter from command line
        String param1 = args[1];
//        System.out.println("The second parameter was: " + param1);
        imgRef = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Read in the specified image
        imgObj = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        String pathref="./"+args[0];
        readImageRGBOrg(width, height, pathref, imgRef);
        for (int i = 1; i < args.length; i++) {
            String imagePath_obj = "./"+args[i];

            int [] [] resulti=readImageRGB(width, height, imagePath_obj, imgObj);
            readImageRGBREF(width, height, pathref, imgRef,resulti);


        }





        // Use label to display the image
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        lbIm1 = new JLabel(new ImageIcon(imgRef));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ImageDisplay ren = new ImageDisplay();
        ren.showIms(args);
    }

}

class IslandInfo {
    int numOnes = 0;
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;
}
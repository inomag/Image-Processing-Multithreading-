import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String SOURCE = "./resource/test.jpg";
    public static final String DEST = "./resource/result.jpg";
    public static final String DEST1 = "./resource/result1.jpg";



    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage original = ImageIO.read(new File(SOURCE));
        BufferedImage result = new BufferedImage(original.getWidth(),original.getHeight(),BufferedImage.TYPE_INT_RGB);
        long start = System.currentTimeMillis();
        recolorSingleThreaded(original,result);
        long end = System.currentTimeMillis();
        System.out.println(end-start);

        BufferedImage result1 = new BufferedImage(original.getWidth(),original.getHeight(),BufferedImage.TYPE_INT_RGB);
        long start1 = System.currentTimeMillis();
        recolorMultiThread(original,result1,4);
        long end1 = System.currentTimeMillis();
        System.out.println(end1-start1);

        File output = new File(DEST);
        File output1 = new File(DEST1);
        ImageIO.write(result,"jpg",output);
        ImageIO.write(result1,"jpg",output1);

    }

    public static void recolorMultiThread(BufferedImage original,BufferedImage result,int noOfThread) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        int width = original.getWidth();
        int height = original.getHeight()/noOfThread;

        for(int i=0;i<noOfThread;i++){
            int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int leftC = 0;
                int topC = height*threadMultiplier;

                recolorImage(original,result,leftC,topC, width, height);
            });
            threads.add(thread);
        }

        for(Thread thread : threads){
            thread.start();
        }
        for(Thread thread : threads){
            thread.join();
        }

    }

    public static void recolorSingleThreaded(BufferedImage original,BufferedImage result){
        recolorImage(original,result,0,0,original.getWidth(), original.getHeight());
    }

    public static void recolorImage(BufferedImage original,BufferedImage result,int leftC, int topC,int W, int H){
        for(int x=leftC;x<leftC+W&&x<original.getWidth();x++){
            for(int y=topC;y<topC+H&&y< original.getHeight();y++){
                recolorPixel(original,result,x,y);
            }
        }
    }

    public static void recolorPixel(BufferedImage original,BufferedImage result,int x,int y){
        int rgb = original.getRGB(x,y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed,newBlue,newGreen;
        if(isShadeGrey(red,green,blue)){
            newRed = Math.min(255,red+20);
            newBlue = Math.max(0,blue-50);
            newGreen = green;
        }else{
            newGreen = green;
            newBlue = blue;
            newRed = red;
        }

        int newRgb = createRGB(newRed,newGreen,newBlue);
        setRGB(result,x,y,newRgb);
    }

    public static void setRGB(BufferedImage image,int x,int y,int rgb){
        image.getRaster().setDataElements(x,y,image.getColorModel().getDataElements(rgb,null));
    }

    public static boolean isShadeGrey(int red,int green,int blue){
        return Math.abs(red-green)<30 && Math.abs(green-blue)<30 && Math.abs(blue-red)<30;
    }

    public static int createRGB(int red,int green,int blue){
        int rgb =0;
        rgb|=blue;
        rgb|=green << 8;
        rgb|=red << 16;

        rgb|=0xFF000000;
        return rgb;
    }


    public static int getRed(int rgb){
        return (rgb & 0x00FF0000) >> 16;
    }
    public static int getGreen(int rgb){
        return (rgb & 0x0000FF00) >> 8;
    }
    public static int getBlue(int rgb){
        return rgb & 0x000000FF;
    }
}

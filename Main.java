import java.io.*;
import javax.sound.sampled.*;

public class Main {
    public static void main(String[] args){
        /* スキャナを開く */
        java.util.Scanner sc = new java.util.Scanner(System.in);
        System.out.println("停止する秒数を指示してください.");

        int sleepSecond = sc.nextInt() * 1000;

        try{
            System.out.println("カウントを開始します。");
            playSound("./start.wav");

            Thread.sleep(sleepSecond);
            /* wavの再生*/
            System.out.println("そこまで。終了です。");
            playSound("./sokomade.wav");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            e.printStackTrace();
        }
    }

    public static void playSound(String filePath) 
        throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        File audioFile = new File(filePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                synchronized (clip) {
                    clip.notifyAll(); // 再生が終了したことを通知
                }
            }
        });

        clip.start(); // オーディオの再生を開始

        synchronized (clip) {
            try{
                clip.wait(); // 再生が完了するまで待機
            } catch (InterruptedException e){
                e.printStackTrace();
            } 
        }

        clip.close(); // リソースを解放
    }
}

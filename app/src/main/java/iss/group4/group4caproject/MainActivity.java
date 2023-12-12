package iss.group4.group4caproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBtns();
        Button fetch = findViewById(R.id.fetchButton);
        fetch.setOnClickListener(this);
    }
    private Integer count = 0;

    protected void setupBtns() {
        int[] ids = new int[20];
        for (int i = 0; i < 20; i++) {
            String imageName = "image" + (i + 1);
            ids[i] = getResources().getIdentifier(imageName, "id", getPackageName());
        }

        for (int i = 0; i < ids.length; i++) {
            ImageButton btn = findViewById(ids[i]);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
    }
    private int clickCount = 0;
    private Set<ImageButton> clickedButtons = new HashSet<>();
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fetchButton)
        {   int[] ids = new int[20];
            for (int i = 0; i < 20; i++) {
            String imageName = "image" + (i + 1);
            ids[i] = getResources().getIdentifier(imageName, "id", getPackageName());
                ImageButton imageButton = findViewById(ids[i]);
                imageButton.setImageResource(R.drawable.deafultimage);
                imageButton.setEnabled(true);
                imageButton.setTag("");
        }
            EditText newURL = findViewById(R.id.urlEditText);
            String inputURL = newURL.getText().toString();
            try {
                startDownloadImage(inputURL);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
        ImageButton imageButton = findViewById(id);
        if (!clickedButtons.contains(imageButton))
        {
            clickedButtons.add(imageButton);
            int realClickCount = clickCount+1;
            clickCount++;
            imageButton.setEnabled(false);
            imageButton.setImageResource(R.drawable.selectedimage);
            checkClickCount();
        }
        }
        }
    private void checkClickCount() {
        if (clickCount == 6) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            Integer imageNumber = 1;
            for (ImageButton button : clickedButtons) {
                intent.putExtra("route"+imageNumber, (String) button.getTag());
                imageNumber = imageNumber+1;
            }
            startActivity(intent);
            clickCount = 0;
            clickedButtons.clear();
        }
    }
    protected void startDownloadImage(String imgURL) throws IOException {
        List<String> imgURLRepository = new ArrayList<>();
        final int[] count = {0};
        int[] ids = new int[20];
        for (int i = 0; i < 20; i++) {
            String imageName = "image" + (i + 1);
            ids[i] = getResources().getIdentifier(imageName, "id", getPackageName());
            String destFilename = UUID.randomUUID().toString() + ".jpg";
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File destFile = new File(dir, destFilename);
            Log.d("MainActivity", "destFile:" + destFile);
            final int index = i;
            // creating a background thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Document document = null;
                    boolean foundRandomImage = false;
                    Element randomImageElement = null;
                    try {
                        document = Jsoup.connect(imgURL).get();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String imageUrl = null;
                    while (!foundRandomImage) {
                        Elements imageElements = document.select("img[src$=.jpg]");
                        randomImageElement = imageElements.get((int) (Math.random() * imageElements.size()));
                        imageUrl = randomImageElement.attr("src");
                        if (!imgURLRepository.contains(imageUrl)) {
                            imgURLRepository.add(imageUrl);
                            // randomImageElement not found in the list
                            foundRandomImage = true;
                        }
                    }
                    ImageDownloader imgDL = new ImageDownloader();
                    if (imgDL.downloadImage(imageUrl, destFile)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
                                ImageButton imageButton = findViewById(ids[index]);
                                imageButton.setImageBitmap(bitmap);
                                imageButton.setTag(destFile.getAbsolutePath());
                                TextView textView = findViewById(R.id.descriptionTextView);
                                ProgressBar progressBar = findViewById(R.id.progressBar);
                                count[0] = count[0] + 1;
                                if (count[0] == 20) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                textView.setText("Downloading " + count[0] + " of 20 images…");
                            }
                        });
                    }
                }
            }).start();
        }
    }
}



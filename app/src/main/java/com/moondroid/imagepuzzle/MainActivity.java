package com.moondroid.imagepuzzle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PuzzleItem> items;
    MyAdapter adapter;
    ImageDivider divider = new ImageDivider();
    Bitmap originBitmap;
    ImageView ivAnswer;

    int imageNum = 0;
    private long backKeyPressedTime = 0;
    private Toast toast;

    int[][] rooms = new int[][]{
            {4, 6, 8, 7, 9, 2, 5, 3},
            {2, 6, 3, 7, 9, 5, 8, 4},
            {5, 7, 9, 2, 4, 6, 3, 8},
            {4, 8, 9, 2, 7, 6, 5, 3},
            {5, 6, 3, 9, 7, 8, 4, 2},
            {6, 8, 3, 2, 9, 4, 7, 5},
            {7, 3, 4, 2, 9, 5, 6, 8},
            {3, 7, 9, 8, 5, 4, 6, 2},
            {9, 5, 4, 2, 3, 7, 8, 6}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler2);
        ivAnswer = findViewById(R.id.iv_answer);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setItem();

    }

    public void setItem() {
        Thread t = new Thread() {
            @Override
            public void run() {
                if (imageNum >= ImageValue.imgUrls.size()) {
                    imageNum = 0;
                }
                String imageUrl = ImageValue.imgUrls.get(imageNum);

                try {
                    //무지개로드를 열어주는 해임달 객체 생성
                    URL url = new URL(imageUrl);
                    //무지개로드 얻어오기
                    InputStream inputStream = url.openStream();
                    //Stream을 통해 읽어들인 파일 데이터를
                    //이미지를 가지는 객체로 생성
                    originBitmap = BitmapFactory.decodeStream(inputStream);
                    Bitmap[] bitmapArray = divider.divideSquare(originBitmap, 3);

                    Random rnd = new Random();
                    int roomNumber = rnd.nextInt(9);

                    int[] room = rooms[roomNumber];

                    items = new ArrayList<>();

                    items.add(new PuzzleItem(bitmapArray[0], 0));
                    items.add(new PuzzleItem(null, 10));
                    items.add(new PuzzleItem(null, 10));
                    items.add(new PuzzleItem(null, 10));
                    for (int i = 0; i < room.length; i++) {
                        items.add(new PuzzleItem(bitmapArray[room[i] - 1], room[i]));
                    }
                    adapter = new MyAdapter(MainActivity.this, items);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                            recyclerView.setAdapter(adapter);
                        }
                    });


                    imageNum++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

    }

    public void clickOriginPicture(View view) {
        OriginPictureDialog dialog = new OriginPictureDialog(this, originBitmap);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void clickOtherPicture(View view) {
        setItem();
    }

    public void clickUpload(View view) {
        startActivity(new Intent(this, ImagePickerActivity.class));
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {
        Context context;
        ArrayList<PuzzleItem> items;
        int blank = 3;

        public MyAdapter(Context context, ArrayList<PuzzleItem> items) {
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.imageView.setImageBitmap(items.get(position).getBitmap());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class VH extends RecyclerView.ViewHolder {

            ImageView imageView;

            public VH(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.iv2);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        changeImage(position);
                    }
                });
            }

            public void changeImage(int position) {
                PuzzleItem item = items.get(position);
                switch (position) {
                    case 0:
                        if (blank == 3) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                            int isFinish = 0;
                            for (int i = 4; i < 12; i++) {
                                if (items.get(i).getIndex() != i - 2) break;
//                                Toast.makeText(context, "" + items.get(i).getIndex(), Toast.LENGTH_SHORT).show();
                                isFinish++;

                            }
                            if (isFinish == 8) {
                                ivAnswer.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ivAnswer.setVisibility(View.GONE);
//                                        ImageValue.Score += 20;
                                        setItem();
                                    }
                                }, 3000);
                            }

                        }
                        break;
                    case 3:
                        if (blank == 0 || blank == 4 || blank == 6) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 4:
                        if (blank == 3 || blank == 5 || blank == 7) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 5:
                        if (blank == 4 || blank == 8) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 6:
                        if (blank == 3 || blank == 7 || blank == 9) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 7:
                        if (blank == 4 || blank == 6 || blank == 8 || blank == 10) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 8:
                        if (blank == 5 || blank == 7 || blank == 11) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 9:
                        if (blank == 6 || blank == 10) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 10:
                        if (blank == 7 || blank == 9 || blank == 11) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                    case 11:
                        if (blank == 8 || blank == 10) {
                            items.remove(blank);
                            items.add(blank, item);
                            items.remove(position);
                            items.add(position, new PuzzleItem(null, 10));
                            notifyDataSetChanged();
                            blank = position;
                        }
                        break;
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }
}



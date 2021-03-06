package dk.danteater.danteater.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import dk.danteater.danteater.R;
import dk.danteater.danteater.circle_indicator.CirclePageIndicator;
import dk.danteater.danteater.circle_indicator.PageIndicator;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.excercise.VideoPlay;
import dk.danteater.danteater.my_plays.DrawerActivity;


public class GuideStartup extends Activity {

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private PageIndicator mIndicator;
    private WMTextView btnStartUp,btnSkip;
    private int currentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide);
        getActionBar().hide();


        viewPager = (ViewPager)findViewById(R.id.guideSlider);
        btnStartUp=(WMTextView)findViewById(R.id.btnStartUp);
        btnSkip=(WMTextView)findViewById(R.id.btnSkip);
        mIndicator = (CirclePageIndicator)findViewById(R.id.guideIndicator);
        int[]   guideImages={R.drawable.tutorial1,R.drawable.tutorial2,R.drawable.tutorial3,R.drawable.tutorial4};
        pagerAdapter= new GuideAdapter(this, guideImages);
        viewPager.setAdapter(pagerAdapter);
        mIndicator.setViewPager(viewPager);
        btnSkip.setVisibility(View.VISIBLE);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage=i;
//                int item=viewPager.getCurrentItem();
                if(currentPage==3) {
                    btnStartUp.setVisibility(View.VISIBLE);
                    btnSkip.setVisibility(View.GONE);
                } else {
                    btnStartUp.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            public final int getCurrentPage() {
                return currentPage;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        btnStartUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(GuideStartup.this, DrawerActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(GuideStartup.this, DrawerActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public class GuideAdapter extends PagerAdapter {

        Context context;
        LayoutInflater inflater;
        int[] guideImages;
        public GuideAdapter(Context context, int[] guideImages) {
            this.context = context;
            this.guideImages=guideImages;
        }

        @Override
        public int getCount() {
            return guideImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView guideImage=new ImageView(context);
            guideImage.setImageResource(guideImages[position]);
            ((ViewPager) container).addView(guideImage, 0);
            guideImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ia = new Intent(GuideStartup.this, VideoPlay.class);
                    ia.putExtra("video_path", R.raw.tutorial);
                    startActivity(ia);
                }
            });
            return guideImage;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }


}

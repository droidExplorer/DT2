package wm.com.danteater.tab_read;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.AppConstants;

/**
 * Created by dhruvil on 30-09-2014.
 */
public class CellReadPlayInfo {

    WMTextView tvInfo;

    public CellReadPlayInfo(View view,Context context) {

        tvInfo = (WMTextView)view.findViewById(R.id.readPlayInfoCellDescription);


    }

    public void setupForPlayLine(PlayLines playline){

        tvInfo.setText("");

        ArrayList<TextLines> textLines = playline.textLinesList;

        if (textLines == null || textLines.size() == 0) {

            tvInfo.setText("");
            return;


        } else if (textLines.size() == 1) {

            TextLines textLine = textLines.get(0);
            tvInfo.setTextColor(Color.parseColor(AppConstants.infoTextColor));
            tvInfo.setText(textLine.LineText);
            return;
        }

        for (TextLines textLine : textLines) {

            String s = tvInfo.getText().toString();
            if (s.length() == 0) {
                tvInfo.setText(textLine.currentText());
            } else {
                tvInfo.setText(s + "\n\n" + textLine.currentText());
            }
            tvInfo.setTextColor(Color.parseColor(AppConstants.infoTextColor));
        }


    }
}

package dk.danteater.danteater.tab_music;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import dk.danteater.danteater.Play.SongFiles;
import dk.danteater.danteater.R;
import dk.danteater.danteater.customviews.WMTextView;


/**
 * Created by nirav on 01-10-2014.
 */
public class CellScriptTableView {

    Context context;
    WMTextView scriptTitle;


    ImageView scriptView;
    public CellScriptTableView(View convertView,Context context) {
        this.context=context;
        scriptTitle=(WMTextView)convertView.findViewById(R.id.script_table_view_cell_title);
        scriptView=(ImageView)convertView.findViewById(R.id.scriptView);

    }

   public void setUpScriptFile(int section,final SongFiles songFile,final String sectionTitle,final Context context){

       scriptTitle.setText(sectionTitle+"");
       scriptView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

                        Intent i=new Intent(context,ReadChord.class);
                        i.putExtra("file_url", songFile.SongMp3Url+"" );
                        context.startActivity(i);

           }
       });
    }




}

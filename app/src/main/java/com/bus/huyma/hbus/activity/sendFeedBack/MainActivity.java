package com.bus.huyma.hbus.activity.sendFeedBack;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bus.huyma.hbus.R;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MainActivity extends AppCompatActivity {
    Session session = null;
    ProgressDialog pdialog = null;
    Context context = null;
    Button btsend = null;
    Button btdialog = null;
    String name, mail, addr, phonenumber, content, Fullcontent, path,filename;
    private static final int REQUEST_PICK_FILE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_VIDEO = 3;

    private File selectedFile,imagefile,videofile;
    int i,j,z=0;
    String[] Filesattach =new String[20];
    String[] Pictureattach = new String[20];
    String[] Videoattach = new String[20];
    ArrayList<Item_attach> listitem = new ArrayList<Item_attach>();
    ArrayList<Info_main> listinfo = new ArrayList<Info_main>();
    ListView listview = null;
    ListView listview_main= null;
    String name_testsss;
    Button tests = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        btsend = (Button) findViewById(R.id.btsend);
        listview_main = (ListView) findViewById(R.id.lsviewmain);
        set_data_info();
        Adapter_info_listview adapter_info_listview = new Adapter_info_listview(MainActivity.this,R.layout.custom_layout_main,listinfo);
        listview_main.setAdapter(adapter_info_listview);

        btdialog = (Button) findViewById(R.id.btdialog);
        setdata();

        btdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                View dialog_layout = inflater.inflate(R.layout.dialoglayout,null);
                AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this);
                db.setView(dialog_layout);
                TextView title = new TextView(MainActivity.this);
                title.setText("Attachment");
                //title.setBackgroundResource(R.drawable.gradient);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(23);
                title.setTextColor(Color.parseColor("#125688"));
                db.setCustomTitle(title);

                listview = (ListView) dialog_layout.findViewById(R.id.LVdialog);
                Adapter_Attach_listview adapter_listview = new Adapter_Attach_listview(MainActivity.this, R.layout.customlayoutfordialouge, listitem);
                listview.setAdapter(adapter_listview);
                adapter_listview.notifyDataSetChanged();
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String check = null;
                        TextView txtview = (TextView) view.findViewById(R.id.txtView);
                        check = txtview.getText().toString();
                        if (check == "Chọn ảnh với camera") {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            imagefile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG" + timeStamp + ".jpg");
                            Uri uri = Uri.fromFile(imagefile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        }
                        if (check == "Chọn tập tin")
                        {
                            Intent intent = new Intent(MainActivity.this, FilePicker.class);
                            startActivityForResult(intent, REQUEST_PICK_FILE);
                        }
                        if(check == "Chọn video với camera video")
                        {
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            videofile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "VID"+timeStamp + ".mp4");
                            Uri uri = Uri.fromFile(videofile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent,REQUEST_VIDEO);
                        }
                    }
                });


                db.setPositiveButton("OK", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Đính kèm thành công",Toast.LENGTH_SHORT).show();
                            }
                        });
                db.setNegativeButton("Cancel", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Filesattach = null;
                            Pictureattach = null;
                            Videoattach = null;
                            Toast.makeText(getApplicationContext(),"Hủy tất cả đính kèm",Toast.LENGTH_SHORT).show();
                        }
                });
            }
        });

        btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getinfo();
                Checkinfo();
            }
        });
    }

    public void getinfo(){
        for (int i =listview_main.getChildCount()-1;i>=0;i-- ) {
            View v = listview_main.getChildAt(i);
            EditText edtext_main = (EditText) v.findViewById(R.id.edtext_main);
            TextView txtview_main = (TextView) v.findViewById(R.id.txtmain);
            String check = txtview_main.getText().toString();
            if(check == "Họ và Tên") {

                name = edtext_main.getText().toString();

            }
            if(check == "Email") {

                mail = edtext_main.getText().toString();

            }
            if(check == "Địa chỉ") {

                addr = edtext_main.getText().toString();

            }
            if(check == "Nội dung") {

                content = edtext_main.getText().toString();

            }
            if(check == "Số điện thoại") {

                phonenumber = edtext_main.getText().toString();

            }
  /*          else {
                String email_test = edtext_main.getText().toString();
                //Toast.makeText(getApplicationContext(),email_test,Toast.LENGTH_SHORT).show();

            }*/

        }
    }
    public void Checkinfo() {

        //Toast.makeText(getApplicationContext(), "Vui lòng nhập Họ và Tên////", Toast.LENGTH_SHORT).show();
        if (name.isEmpty() && addr.isEmpty() && mail.isEmpty() && phonenumber.isEmpty() && content.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đày đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            if (name.isEmpty() || addr.isEmpty() || mail.isEmpty() || phonenumber.isEmpty() || content.isEmpty()) {
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập Họ và Tên", Toast.LENGTH_SHORT).show();
                }

                if (mail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập email của bạn", Toast.LENGTH_SHORT).show();
                }
                if (addr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập địa chỉ của bạn", Toast.LENGTH_SHORT).show();
                }
                if (phonenumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập số điện thoại của bạn", Toast.LENGTH_SHORT).show();
                }
                if (content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                }
            } else {
                putinfomail();
            }
        }

    }

    public void putinfomail() {

        Fullcontent = "Họ và tên: " + name + "\nEmail: " + mail + "\nĐịa chỉ: " + addr + "\nSố điện thoại liên hệ: " +
                phonenumber + "\nNội dung liên hệ: " + content;

        Properties props = new Properties();
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("huymapmap41@gmail.com", "0982851413Huy@");
            }
        });

        pdialog = ProgressDialog.show(context, "", "Đang gủi Mail...", true);

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("huymapmap41@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("huymapmap40@gmail.com"));
                message.setSubject("PHẢN ÁNH - GÓP Ý XE BUÝT ["+name.toUpperCase()+"]");
                //message.setContent(Fullcontent, "text/html; charset=utf-8");
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(Fullcontent);

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);
                //Attach file
                if (Filesattach != null) {
                    for (int t=0;t<i;t++) {
                        path = Filesattach[t];
                        xulyxhuoi();

                        messageBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(path);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(filename);
                        multipart.addBodyPart(messageBodyPart);
                    }
                }
                if(Pictureattach !=null)
                {
                    for (int k=0;k<j;k++)
                    {
                        path = Pictureattach[k];
                        xulyxhuoi();
                        messageBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(path);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(filename);
                        multipart.addBodyPart(messageBodyPart);
                    }
                }
                if(Videoattach !=null)
                {
                    for (int k=0;k<z;k++)
                    {
                        path = Videoattach[k];
                        xulyxhuoi();
                        messageBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(path);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(filename);
                        multipart.addBodyPart(messageBodyPart);
                    }
                }


                // Send the complete message parts
                message.setContent(multipart);
                Transport.send(message);
                //Toast.makeText(getApplicationContext(), "Dangsend", Toast.LENGTH_LONG).show();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pdialog.dismiss();
            Toast.makeText(getApplicationContext(), "Đã gửi thành công!!", Toast.LENGTH_LONG).show();
            i =0;
            j =0;
            z=0;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_PICK_FILE:


                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        selectedFile = new File
                                (data.getStringExtra(FilePicker.EXTRA_FILE_PATH));

                        Filesattach[i] = selectedFile.getPath();

                        //attachFiles[i] = selectedFile.getPath();
                        //path = selectedFile.getPath();
                        Toast.makeText(getApplicationContext(),selectedFile.getPath(), Toast.LENGTH_SHORT).show();
/*                        tempt[i] = attachFiles[i];
                        if(i != 0)
                        {
                            for(int k=1;k<=i;k++) {
                                attachFiles[i - k] = tempt[i - k];
                            }
                            Toast.makeText(getApplicationContext(),attachFiles[1], Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getApplicationContext(),attachFiles[0], Toast.LENGTH_SHORT).show();*/


                        i++;

                    }

                    break;
                case REQUEST_IMAGE_CAPTURE:
                    if (imagefile.exists())
                    {
                        Pictureattach[j]=imagefile.getAbsolutePath();
                        Toast.makeText(getApplicationContext(),imagefile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                        j++;
                    }

                    break;
                case REQUEST_VIDEO:
                    if(videofile.exists())
                    {
                        Videoattach[z]=videofile.getAbsolutePath();
                        Toast.makeText(getApplicationContext(),videofile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                        z++;
                    }
            }
        }
    }
    public void xulyxhuoi(){
        filename = path;
        filename=filename.substring(filename.lastIndexOf("/")+1);
    }

    public void setdata(){
        Item_attach item1 = new Item_attach();
        item1.setName("Chọn tập tin");
        item1.setImage(R.drawable.fileicon);
        Item_attach item2 = new Item_attach();
        item2.setName("Chọn ảnh với camera");
        item2.setImage(R.drawable.cameraicon);
        Item_attach item3 = new Item_attach();
        item3.setImage(R.drawable.videoicon);
        item3.setName("Chọn video với camera video");
        Item_attach item4 = new Item_attach();

        listitem.add(item1);
        listitem.add(item2);
        listitem.add(item3);
    }
    public void set_data_info()
    {
        Info_main info1 = new Info_main();
        info1.setName("Họ và Tên");
        Info_main info2 = new Info_main();
        info2.setName("Email");
        Info_main info3 = new Info_main();
        info3.setName("Địa chỉ");
        Info_main info4 = new Info_main();
        info4.setName("Số điện thoại");
        Info_main info5 = new Info_main();
        info5.setName("Nội dung");
        listinfo.add(info1);
        listinfo.add(info2);
        listinfo.add(info3);
        listinfo.add(info4);
        listinfo.add(info5);
    }
}

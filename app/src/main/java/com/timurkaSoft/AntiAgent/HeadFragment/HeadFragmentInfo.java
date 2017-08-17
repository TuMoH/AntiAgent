package com.timurkaSoft.AntiAgent.HeadFragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonFloat;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.timurkaSoft.AntiAgent.ComplainDialog;
import com.timurkaSoft.AntiAgent.HtmlHelper;
import com.timurkaSoft.AntiAgent.MainActivity;
import com.timurkaSoft.AntiAgent.MoreInfo;
import com.timurkaSoft.AntiAgent.R;
import com.timurkaSoft.AntiAgent.photos.ActPhotos;
import com.timurkaSoft.AntiAgent.photos.ImageRecyclerAdapter;
import com.timurkaSoft.AntiAgent.photos.ItemClickListener;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HeadFragmentInfo extends Fragment {

    TextView tvHead;
    TextView tvAuthor;
    TextView tvAllInfo;
    ImageView telImageView;
    LinearLayout telImageViewLayout;
    public static MoreInfo moreInfo;
    TextView tvProgress;
    ProgressWheel progressBar;
    Boolean hasProgress = false;
    ParseSite asyncTask;

    String cityFoTel = "krd";
    String id = "1";

    RecyclerView recycler;
    ImageRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.head_fragment_info, container, false);

        tvHead = (TextView) view.findViewById(R.id.tvHead);
        tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
        tvAllInfo = (TextView) view.findViewById(R.id.tvAllInfo);
        telImageView = (ImageView) view.findViewById(R.id.tel_image_view);
        telImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCall();
            }
        });
        telImageViewLayout = (LinearLayout) view.findViewById(R.id.tel_image_view_layout);
        tvProgress = (TextView) view.findViewById(R.id.tvProgress);
        progressBar = (ProgressWheel) view.findViewById(R.id.progressBar);

        asyncTask = new ParseSite();
        asyncTask.execute(MainActivity.shortInfo.getHref());

        recycler = (RecyclerView) view.findViewById(R.id.photo_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);

        adapter = new ImageRecyclerAdapter();
        recycler.setAdapter(adapter);
        recycler.addOnItemTouchListener(new ItemClickListener(getActivity(), new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intentImg = new Intent(getActivity(), ActPhotos.class);
                intentImg.putExtra("img_position", position);
                startActivity(intentImg);
            }
        }));

        return view;
    }

    public void refresh() {
        asyncTask = new ParseSite();
        asyncTask.execute(MainActivity.shortInfo.getHref());
    }

    @Override
    public void onStop() {
        super.onStop();
        asyncTask.cancel(true);
    }

    public void goToGeo() {
        if (hasProgress()) {
            try {
                String geo;
                try {
                    geo = moreInfo.getAllInfo().split("Адрес: ")[1].split("\n")[0];
                } catch (Exception e) {
                    geo = "";
                }
                String geoUriString = "geo:0,0?q=" + getCityRus(MainActivity.shortInfo.getHref()) + geo + "&z=8";
                Intent intentMap = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUriString));
                startActivity(intentMap);
            } catch (Exception e) {
                Snackbar.with(getActivity())
                        .text("Необходимо приложение для просмотра карт")
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .show(getActivity());
            }
        }
    }

    public void goToCall() {
        if (hasProgress()) {
            try {
                String phoneNumber;
                try {
                    phoneNumber = "tel:" + moreInfo.getTel();
                } catch (Exception e) {
                    phoneNumber = "tel:";
                }
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse(phoneNumber));
                startActivity(intentCall);
            } catch (Exception e) {
                Snackbar.with(getActivity())
                        .text("Необходимо приложение для совершения вызовов")
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .show(getActivity());
            }
        }
    }

    public void goToPhotos() {
        if (hasProgress()) {
            try {
                if (moreInfo.getImg().size() > 0) {
                    Intent intentImg = new Intent(getActivity(), ActPhotos.class);
                    intentImg.putExtra("href", MainActivity.shortInfo.getHref());
                    startActivity(intentImg);
                }
            } catch (Exception e) { /*NOP*/}
        }
    }

    public void complaint() {
        if (hasProgress()) {
            final ComplainDialog dialog = new ComplainDialog(getActivity());
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder url = new StringBuilder();
                    url.append(MainActivity.shortInfo.getHref().split(".ru/")[0]);
                    url.append(".ru/ajax.php?func=complain&type=");
                    url.append(dialog.getType());
                    url.append("&id=");
                    url.append(MainActivity.shortInfo.getHref().split("&details=")[1]);
                    url.append("&comment=");
                    url.append(dialog.getComment());
                    Log.e("MY", url.toString());
                    new Send().execute(url.toString());
                }
            });
            dialog.show();
        }
    }

    private boolean hasProgress() {
        if (hasProgress) {
            Snackbar.with(getActivity())
                    .text("Дождитесь окончания загрузки")
                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                    .show(getActivity());
            return false;
        }
        return true;
    }

    private class Send extends AsyncTask<String, Void, Boolean> {
        String url;

        @Override
        protected Boolean doInBackground(String... url) {
            this.url = url[0];
            try {
                HtmlHelper hh = new HtmlHelper(new URL(url[0]));
                if (hh.getResponse())
                    return true;
            } catch (Exception e) { /* NOP */ }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b)
                Snackbar.with(getActivity())
                        .text("Жалоба отправлена. Спасибо!")
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .show(getActivity());
            else
                Snackbar.with(getActivity())
                        .text("Не удалось отправить жалобу")
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                        .actionLabel("Повторить")
                        .actionColor(Color.YELLOW)
                        .actionLabelTypeface(Typeface.DEFAULT_BOLD)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                new Send().execute(url);
                            }
                        })
                        .show(getActivity());
        }
    }

    private class ParseSite extends AsyncTask<String, Void, String> {

        boolean error;
        boolean phoneError;
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            tvHead.setVisibility(View.GONE);
            tvAuthor.setVisibility(View.GONE);
            tvAllInfo.setVisibility(View.GONE);
            telImageViewLayout.setVisibility(View.GONE);
            moreInfo = null;
            error = false;
            phoneError = false;
            bitmap = null;
            hasProgress = true;
            tvProgress.setText("Загружаю объявление");
            tvProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... arg) {
            try {
                cityFoTel = getCityFoTel(arg[0]);
                id = getID(arg[0]);
                String tel = null;
                try {
                    File file = Glide
                            .with(getActivity())
                            .load(getTelImgUrl()).downloadOnly(-1, -1).get();

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inDensity = DisplayMetrics.DENSITY_DEFAULT;

                    if (opts.inTargetDensity == 0) {
                        opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
                    }

                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                    tel = MainActivity.phoneParser.extractText(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    phoneError = true;
                }
                HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
                moreInfo = new MoreInfo.Builder()
                        .setHead(hh.getDivByClass("b-serp-item__header_bold"))
                        .setAuthor(hh.getDivByClass("b-serp-item__address-text").trim())
                        .setAllInfo(cleanAllInfo(hh.getDivByClass("b-card__content").trim(), tel))
                        .setTel(tel)
                        .setImg(hh.getImg())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String output) {
            try {
                tvHead.setText(moreInfo.getHead());
                tvAuthor.setText(moreInfo.getAuthor());
                tvAllInfo.setText(boldText(moreInfo.getAllInfo()));
                telImageView.setImageBitmap(bitmap);

                if (moreInfo.getImg().size() > 0) {
                    ((ButtonFloat) getActivity().findViewById(R.id.subFabPhoto))
                            .setIconDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
                } else
                    ((ButtonFloat) getActivity().findViewById(R.id.subFabPhoto))
                            .setIconDrawable(getResources().getDrawable(R.drawable.ic_menu_camera_off));
            } catch (Exception e) {
                tvProgress.setText("Объявление удалено.");
            }
            try {
                if (!error) {
                    tvProgress.setVisibility(View.GONE);
                    tvHead.setVisibility(View.VISIBLE);
                    tvAuthor.setVisibility(View.VISIBLE);
                    tvAllInfo.setVisibility(View.VISIBLE);
                    telImageViewLayout.setVisibility(View.VISIBLE);
                } else
                    tvProgress.setText("Не удалось загрузить объявление. Ошибка соединения.");

                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (MainActivity.tinydb.getBoolean("no_images") || moreInfo.getImg().isEmpty()) {
                    recycler.setVisibility(View.GONE);
                } else {
                    recycler.setVisibility(View.VISIBLE);
                    adapter.setUrls(moreInfo.getImg());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            hasProgress = false;
        }

        private String cleanAllInfo(String allInfo, String tel) {
            while (allInfo.contains("    "))
                allInfo = allInfo.replaceAll("    ", "  ");
            allInfo = allInfo.replace("  ", "\n");
            while (allInfo.contains("\n\n"))
                allInfo = allInfo.replaceAll("\n\n", "\n");
            allInfo = allInfo.replaceAll("\n ", "\n");
            allInfo = allInfo.replace("Телефон:\n", tel != null ? "Телефон: " + tel + "\n" : "");
            allInfo = allInfo.replace(", Email: var login\n= '", "Email: ");
            allInfo = allInfo.replace("';var server = '", "@");
            allInfo = allInfo.replace("';var email\n= login+'@'+server;var url = 'mailto:'+email;document.write('<a href=\"'+url+'\">'+email+'</a>');", "");
            allInfo = allInfo.replace(", просмотров:", ", просмотров");
            allInfo = allInfo.replace("&nbsp;", " ");
            return allInfo;
        }

        private String getTelImgUrl() {
            return "https://" + cityFoTel + ".antiagent.ru/phone.php?id=" + id;
        }

        private String getID(String href) {
            String[] split = href.split("=");
            if (split.length > 0)
                return split[split.length - 1];
            return "1";
        }

        private Spannable boldText(String s) {
            Spannable text = new SpannableString(s);
            int i0 = 0;
            for (int i = 0; i < text.length(); i++) {
                if ((s.charAt(i) == '\n') || (i == (text.length() - 1))) {
                    if ((i - i0) < 30) {
                        text.setSpan(new StyleSpan(Typeface.BOLD), i0, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        text.setSpan(new ForegroundColorSpan(Color.GRAY), i0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    i0 = i;
                } else if ((s.charAt(i) == ':') && ((s.charAt(i + 1)) == ' ')) {
                    i0 = i;
                }
            }
            return text;
        }
    }

    private String getCity(String href) {
        if (href.contains("krd")) return "krasnodar";
        if (href.contains("msk")) return "moskva";
        if (href.contains("smr")) return "";
        if (href.contains("vrn")) return "";
        if (href.contains("prm")) return "";
        if (href.contains("ekb")) return "ekaterinburg";
        if (href.contains("rnd")) return "";
        if (href.contains("kzn")) return "";
        if (href.contains("spb")) return "spburg";
        if (href.contains("nnov")) return "";
        if (href.contains("ufa")) return "";
        if (href.contains("chel")) return "";
        if (href.contains("nsk")) return "";
        return null;
    }

    private String getCityFoTel(String href) {
        if (href.contains("krd")) return "krd";
        if (href.contains("msk")) return "msk";
        if (href.contains("smr")) return "smr";
        if (href.contains("vrn")) return "vrn";
        if (href.contains("prm")) return "prm";
        if (href.contains("ekb")) return "ekb";
        if (href.contains("rnd")) return "rnd";
        if (href.contains("kzn")) return "kzn";
        if (href.contains("spb")) return "spb";
        if (href.contains("nnov")) return "nnov";
        if (href.contains("ufa")) return "ufa";
        if (href.contains("chel")) return "chel";
        if (href.contains("nsk")) return "nsk";
        return null;
    }

    private String getCityRus(String href) {
        if (href.contains("krd")) return "Краснодар, ";
        if (href.contains("msk")) return "Москва, ";
        if (href.contains("ekb")) return "Екатеринбург, ";
        if (href.contains("spb")) return "Санкт-Петербург, ";
        return null;
    }

}

package dev.brian.facedemo.fragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dev.brian.facedemo.R;
import dev.brian.facedemo.util.Constant;
import dev.brian.facedemo.util.DrawUtil;
import dev.brian.facedemo.util.GifView;
import dev.brian.facedemo.util.HttpUtils;
import dev.brian.facedemo.util.ImageUtil;


/**
 * 驾照识别
 * 
 * @author Administrator
 * 
 */
@SuppressLint({ "NewApi", "HandlerLeak" })
public class OcrdriverlicenseFragment extends Fragment implements OnClickListener {
	private ImageView iv_ocrdriverlicense;
	private Button ib_ocrdriverlicense_enter;
	private Button ib_ocrdriverlicense_choice;

	private TextView tv_ocrdriverlicense;

	private Bitmap scalingPhoto;// 位图

	private String ocrdriverlicense;

	private Paint paint;// 画笔工具
	private View view;
	private GifView gif;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_ocrdriverlicense, container, false);

		iv_ocrdriverlicense = (ImageView) view.findViewById(R.id.iv_ocrdriverlicense);
		ib_ocrdriverlicense_enter = (Button) view.findViewById(R.id.ib_ocrdriverlicense_enter);
		ib_ocrdriverlicense_choice = (Button) view.findViewById(R.id.ib_ocrdriverlicense_choice);

		tv_ocrdriverlicense = (TextView) view.findViewById(R.id.tv_ocrdriverlicense);
		gif = (GifView) view.findViewById(R.id.gif);
		ib_ocrdriverlicense_enter.setOnClickListener(this);
		ib_ocrdriverlicense_choice.setOnClickListener(this);

		paint = new Paint();// 创建画笔

		scalingPhoto = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.ocrdriverlicense);
		return view;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String str = (String) msg.obj;
			System.out.println("********ocrdriverlicense:" + str);
			if (str.equals("403") || str.equals("400") || str.equals("413")
					|| str.equals("500")) {
				Toast.makeText(getActivity(), "Please Try Again",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject resultJSON = new JSONObject(str);
					System.out.println(resultJSON.getString("cards") + "=====");
					if (resultJSON.getString("cards").equals("[]")) {
						Toast.makeText(getActivity(),
								"Try Again", Toast.LENGTH_SHORT)
								.show();
					} else {
						ocrdriverlicense = DrawUtil.ocrdriverlicensePrepareBitmap(resultJSON,
								scalingPhoto, paint, iv_ocrdriverlicense);
	
						System.out.println("------------ocrdriverlicense" + ocrdriverlicense);
						
						tv_ocrdriverlicense.setText("驾照信息:" + ocrdriverlicense);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			gif.setVisibility(View.GONE);// 停止gif
		};
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			//
			if (data != null) {
				//
				String photoPath = ImageUtil.getPhotoPath(getActivity(), data);
				//
				scalingPhoto = ImageUtil.getScalingPhoto(photoPath);
				//
				iv_ocrdriverlicense.setImageBitmap(scalingPhoto);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_ocrdriverlicense_choice:
			//
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, 1);//
			break;
		case R.id.ib_ocrdriverlicense_enter:
			// 显示加载动画
			gif.setVisibility(View.VISIBLE);
			gif.setMovieResource(R.raw.red); // 设置背景gif图片资源
			// 确定按钮不允许再次点击

			//
			String base64ImageEncode = ImageUtil
					.getBase64ImageEncode(scalingPhoto);
			System.out.println(base64ImageEncode);
			//
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("api_key", Constant.Key);
			map.put("api_secret", Constant.Secret);
			map.put("image_base64", base64ImageEncode);

			//
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String result = HttpUtils.post(Constant.URL_OCRDRIVERLICENSE,
								map);

						Message message = new Message();
						message.obj = result;
						handler.sendMessage(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			break;
		}
	}
}

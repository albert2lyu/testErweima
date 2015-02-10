package cn.teserweima;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import com.swetake.util.Qrcode;

public class TwoDimensionCode extends JFrame implements ActionListener {

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content
	 *            存储内容
	 * @param imgPath
	 *            图片路径
	 */
	public void encoderQRCode(String content, String imgPath) {
		this.encoderQRCode(content, imgPath, "png", 7);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content
	 *            存储内容
	 * @param output
	 *            输出流
	 */
	public void encoderQRCode(String content, OutputStream output) {
		this.encoderQRCode(content, output, "png", 7);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content
	 *            存储内容
	 * @param imgPath
	 *            图片路径
	 * @param imgType
	 *            图片类型
	 */
	public File encoderQRCode(String content, String imgPath, String imgType) {
		return this.encoderQRCode(content, imgPath, imgType, 7);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content
	 *            存储内容
	 * @param output
	 *            输出流
	 * @param imgType
	 *            图片类型
	 */
	public void encoderQRCode(String content, OutputStream output,
			String imgType) {
		this.encoderQRCode(content, output, imgType, 7);
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content
	 *            存储内容
	 * @param imgPath
	 *            图片路径
	 * @param imgType
	 *            图片类型
	 * @param size
	 *            二维码尺寸
	 */
	public File encoderQRCode(String content, String imgPath, String imgType,
			int size) {
		try {
			BufferedImage bufImg = this.qRCodeCommon(content, imgType, size);
			File imgFile = new File(imgPath);
			if (ImageIO.write(bufImg, imgType, imgFile)) {
				return imgFile;
			}
			// 生成二维码QRCode图片

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成二维码(QRCode)图片
	 * 
	 * @param content
	 *            存储内容
	 * @param output
	 *            输出流
	 * @param imgType
	 *            图片类型
	 * @param size
	 *            二维码尺寸
	 */
	public void encoderQRCode(String content, OutputStream output,
			String imgType, int size) {
		try {
			BufferedImage bufImg = this.qRCodeCommon(content, imgType, size);
			// 生成二维码QRCode图片
			ImageIO.write(bufImg, imgType, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成二维码(QRCode)图片的公共方法
	 * 
	 * @param content
	 *            存储内容
	 * @param imgType
	 *            图片类型
	 * @param size
	 *            二维码尺寸
	 * @return
	 */
	private BufferedImage qRCodeCommon(String content, String imgType, int size) {
		BufferedImage bufImg = null;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			// 图片尺寸
			int imgSize = 67 + 12 * (size - 1);
			bufImg = new BufferedImage(imgSize, imgSize,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();
			// 设置背景颜色
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色> BLACK
			gs.setColor(Color.BLACK);
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);// 出错不用关
						}
					}
				}
			} else {
				System.out.println("");
			}
			gs.dispose();
			bufImg.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufImg;
	}

	/**
	 * 解析二维码（QRCode）
	 * 
	 * @param imgPath
	 *            图片路径
	 * @return
	 */
	public String decoderQRCode(String imgPath) {
		// QRCode 二维码图片的文件
		File imageFile = new File(imgPath);
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(imageFile);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new TwoDimensionCodeImage(
					bufImg)), "utf-8");
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (DecodingFailedException dfe) {
			System.out.println("Error: " + dfe.getMessage());
			dfe.printStackTrace();
		}
		return content;
	}

	/**
	 * 解析二维码（QRCode）
	 * 
	 * @param input
	 *            输入流
	 * @return
	 */
	public String decoderQRCode(InputStream input) {
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(input);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new TwoDimensionCodeImage(
					bufImg)), "utf-8");
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (DecodingFailedException dfe) {
			System.out.println("Error: " + dfe.getMessage());
			dfe.printStackTrace();
		}
		return content;
	}

	JButton jButton;

	public TwoDimensionCode() {
		jButton = new JButton("选择文件");
		jButton.addActionListener(this);
		this.add(jButton);
		this.setBounds(400, 200, 100, 100);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	@Override
	public void actionPerformed(ActionEvent paramActionEvent) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.showDialog(new JLabel(), "选择");
		String command = paramActionEvent.getActionCommand();
		if (command.equals("选择文件")) {
			File file = jfc.getSelectedFile();
			if (file.isDirectory()) {
				System.out.println("文件夹:" + file.getAbsolutePath());
			} else if (file.isFile()) {
				start(file.getAbsolutePath());
			}
	
		}
	}

	public static void main(String[] args) {
		new TwoDimensionCode();
	}

	public void start(String args) {

		File file = new File(args);
		String[][] result = null;
		try {
			result = ExcelOperate.getData(file, 1);
		} catch (Exception e) {
			System.out.println("读取excel 错误");
		}
		File file2 = new File(file.getParent(), "tempImage");
		File num = new File(file.getParent(), "numImage");
		File finFile = new File(file.getParent(), "final");
		if (!num.exists()) {
			num.mkdir();
		}
		if (!file2.exists()) {
			file2.mkdir();
		}
		if (!finFile.exists()) {
			finFile.mkdir();
		}
		if (result != null) {
			TwoDimensionCode handler = new TwoDimensionCode();
			int rowLength = result.length;
			for (int i = 0; i < rowLength; i++) {
				String[] nowStrings = result[i];

				String imgPath = file2.getPath() + "/" + nowStrings[1].trim()
						+ ".png";
				String encoderContent = nowStrings[0].trim();
				File erFile = handler.encoderQRCode(encoderContent, imgPath,
						"png");
				System.out.println(erFile.getAbsolutePath() + "创建二维码图片完毕");
				try {
					File numFile = creatImage(num, nowStrings[1]);
					// File file2 = new File(file.getParent(), "tempImage");
					// File num = new File(file.getParent(), "numImage");
					// File finFile = new File(file, "final");
					finalCreateImage(erFile, numFile, finFile);
				} catch (IOException e) {
					// 错误不用关
				}

			}

		}

	}

	/**
	 * 创建编号图片
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private  File creatImage(File savePath, String name)
			throws IOException {
		int width = 139;
		int height = 20;
		File file = new File(savePath, name + ".png");
		Font font = new Font(Font.DIALOG, Font.PLAIN, 10);
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, width, height);
		g2.setPaint(Color.BLACK);

		FontRenderContext context = g2.getFontRenderContext();
		Rectangle2D bounds = font.getStringBounds(name, context);
		// double x = (width - bounds.getWidth()) / 2;
		double y = (height - bounds.getHeight()) / 2;
		// double ascent = -bounds.getY();
		// double baseY = y + ascent;

		g2.drawString(name, 10, 15);

		if (ImageIO.write(bi, "png", file)) {
			System.out.println(file.getAbsolutePath() + "编号图片完毕");
			return file;
		} else
			System.out.println(file.getAbsolutePath() + "编号图片创建失败");
		return null;

	}

	/**
	 * 
	 * @param firstFile
	 *            二维码图片
	 * @param secoFile
	 *            编号图片
	 * @param saveFile
	 *            合成后的输出路径 不带文件名
	 */
	private  void finalCreateImage(File firstFile, File secoFile,
			File saveFile) {
		try {
			// 读取第一张图片

			BufferedImage ImageOne = ImageIO.read(firstFile);
			int width = ImageOne.getWidth();// 图片宽度
			int height = ImageOne.getHeight();// 图片高度
			// 从图片中读取RGB
			int[] ImageArrayOne = new int[width * height];
			ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne,
					0, width);
			// 对第二张图片做相同的处理

			BufferedImage ImageTwo = ImageIO.read(secoFile);
			int width2 = ImageTwo.getWidth();// 图片宽度
			int height2 = ImageTwo.getHeight();// 图片高度
			int[] ImageArrayTwo = new int[width2 * height2];
			ImageArrayTwo = ImageTwo.getRGB(0, 0, width2, height2,
					ImageArrayTwo, 0, width2);
			// 生成新图片
			BufferedImage ImageNew = new BufferedImage(width, height + height2,
					BufferedImage.TYPE_INT_RGB);
			ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width);// 设置上半部分的RGB
			ImageNew.setRGB(0, height, width2, height2, ImageArrayTwo, 0, width);// 设置下半部分的RGB

			ImageIO.write(ImageNew, "png",
					new File(saveFile, firstFile.getName()));// 写图片
			System.out.println(firstFile.getName() + "图片合成完毕");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
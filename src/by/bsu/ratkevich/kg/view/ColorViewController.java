package by.bsu.ratkevich.kg.view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ColorViewController {

	@FXML
	private Circle realColor;

	@FXML
	private Slider colorRgbR;

	@FXML
	private Slider colorRgbB;

	@FXML
	private Slider colorRgbG;

	@FXML
	private Slider colorHlsL;

	@FXML
	private Slider colorHlsS;

	@FXML
	private Slider colorHlsH;

	@FXML
	private Slider colorLabL;

	@FXML
	private Slider colorLabB;

	@FXML
	private Slider colorLabA;

	@FXML
	private Slider colorCmyM;

	@FXML
	private Slider colorCmyC;

	@FXML
	private Slider colorCmyY;

	@FXML
	private Label labelRGB;

	@FXML
	private Label labelHLS;

	@FXML
	private Label labelCMY;

	@FXML
	private Label labelLab;

	@FXML
	void initialize() {
		final ChangeListener<Number> labChangeListener = new ChangeListener<Number>() {

			private boolean inChange = false;

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (inChange) {
					return;
				}
				try {
					inChange = true;
					double lValLab = colorLabL.getValue();
					double aValLab = colorLabA.getValue();
					double bValLab = colorLabB.getValue();

					double yPreVal = (lValLab + 16) / 116.0;
					double xPreVal = aValLab / 500.0 + yPreVal;
					double zPreVal = yPreVal - bValLab / 200.0;

					double delta = 6.0 / 29.0;
					double delta2 = Math.pow(6.0 / 29.0, 2) * 3.0;
					if (Math.pow(yPreVal, 3) > delta) {
						yPreVal = Math.pow(yPreVal, 3);
					} else {
						yPreVal = delta2 * (yPreVal - 4.0 / 29.0);
						//	yPreVal = (yPreVal - 16 / 116.0) / 7.787;
					}
					if (Math.pow(xPreVal, 3) > delta) {
						xPreVal = Math.pow(xPreVal, 3);
					} else {
						xPreVal = delta2 * (xPreVal - 4.0 / 29.0);
						//xPreVal = (xPreVal - 16 / 116.0) / 7.787;
					}
					if (Math.pow(zPreVal, 3) > delta) {
						zPreVal = Math.pow(zPreVal, 3);
					} else {
						zPreVal = delta2 * (zPreVal - 4.0 / 29.0);
						//zPreVal = (zPreVal - 16 / 116.0) / 7.787;
					}
					//Observer= 2°, Illuminant= D65
					double xNorm = 95.047,
							yNorm = 100.000,
							zNorm = 108.883;
					double varX = xNorm * xPreVal / 100.0;
					double varY = yNorm * yPreVal / 100.0;
					double varZ = zNorm * zPreVal / 100.0;

					double rValRgb = varX * 3.2406 + varY * -1.5372 + varZ * -0.4986;
					double gValRgb = varX * -0.9689 + varY * 1.8758 + varZ * 0.0415;
					double bValRgb = varX * 0.0557 + varY * -0.2040 + varZ * 1.0570;

					if (rValRgb > 0.0031308) {
						rValRgb = 1.055 * Math.pow(rValRgb, (1 / 2.4)) - 0.055;
					} else {
						rValRgb = 12.92 * rValRgb;
					}
					if (gValRgb > 0.0031308) {
						gValRgb = 1.055 * Math.pow(gValRgb, (1 / 2.4)) - 0.055;
					} else {
						gValRgb = 12.92 * gValRgb;
					}
					if (bValRgb > 0.0031308) {
						bValRgb = 1.055 * Math.pow(bValRgb, (1 / 2.4)) - 0.055;
					} else {
						bValRgb = 12.92 * bValRgb;
					}

					double rVal = rValRgb * 255.0;
					double gVal = gValRgb * 255.0;
					double bVal = bValRgb * 255.0;
					colorRgbR.setValue(rVal);
					colorRgbG.setValue(gVal);
					colorRgbB.setValue(bVal);
					
				} finally {
					inChange = false;
				}
			}
		};

		final ChangeListener<Number> rgbChangeListener = new ChangeListener<Number>() {

			private boolean inChange = false;

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (inChange) {
					return;
				}
				try {
					inChange = true;
					if (newValue.equals(Double.NaN)) {
						newValue = 0;
					}
					colorLabL.valueProperty().removeListener(labChangeListener);
					colorLabA.valueProperty().removeListener(labChangeListener);
					colorLabB.valueProperty().removeListener(labChangeListener);

					DoubleProperty colorRgbRVal = colorRgbR.valueProperty();
					DoubleProperty colorRgbGVal = colorRgbG.valueProperty();
					DoubleProperty colorRgbBVal = colorRgbB.valueProperty();
					realColor.setFill(
							Color.rgb(colorRgbRVal.intValue(),
									colorRgbGVal.intValue(),
									colorRgbBVal.intValue()));
					double rNormRGB = normalizeRGB(colorRgbRVal);
					double gNormRGB = normalizeRGB(colorRgbGVal);
					double bNormRGB = normalizeRGB(colorRgbBVal);

					colorCmyC.setValue(rgbToCmy(rNormRGB));
					colorCmyM.setValue(rgbToCmy(gNormRGB));
					colorCmyY.setValue(rgbToCmy(bNormRGB));

					double rgbMax = Math.max(Math.max(rNormRGB, bNormRGB), gNormRGB);
					double rgbMin = Math.min(Math.min(rNormRGB, bNormRGB), gNormRGB);
					double delta = rgbMax - rgbMin;

					double hue = 0;
					if (delta == 0) {
						hue = colorHlsH.getValue();
					} else if (rgbMax == rNormRGB) {
						hue = (gNormRGB - bNormRGB) / delta;
						hue += gNormRGB < bNormRGB ? 6 : 0;
					} else if (rgbMax == gNormRGB) {
						hue = (bNormRGB - rNormRGB) / delta + 2;
					} else if (rgbMax == bNormRGB) {
						hue = (rNormRGB - gNormRGB) / delta + 4;
					}
					hue *= 60.0;

					double lightness = (rgbMax + rgbMin) / 2;
					double saturation = 0; //if delta == 0
					if (delta != 0) {
						saturation = delta / (1 - Math.abs(2 * lightness - 1));
					}
					colorHlsH.setValue(hue);
					colorHlsL.setValue(lightness);
					colorHlsS.setValue(saturation);

					///////////////////// TO LAB
					if (rNormRGB > 0.04045) {
						rNormRGB = Math.pow((rNormRGB + 0.055) / 1.055, 2.4);
					} else {
						rNormRGB = rNormRGB / 12.92;
					}
					if (gNormRGB > 0.04045) {
						gNormRGB = Math.pow((gNormRGB + 0.055) / 1.055, 2.4);
					} else {
						gNormRGB = gNormRGB / 12.92;
					}

					if (bNormRGB > 0.04045) {
						bNormRGB = Math.pow((bNormRGB + 0.055) / 1.055, 2.4);
					} else {
						bNormRGB = bNormRGB / 12.92;
					}

					rNormRGB = rNormRGB * 100;
					gNormRGB = gNormRGB * 100;
					bNormRGB = bNormRGB * 100;

					//Observer. = 2°, Illuminant = D65
					double xVal = rNormRGB * 0.4124 + gNormRGB * 0.3576 + bNormRGB * 0.1805;
					double yVal = rNormRGB * 0.2126 + gNormRGB * 0.7152 + bNormRGB * 0.0722;
					double zVal = rNormRGB * 0.0193 + gNormRGB * 0.1192 + bNormRGB * 0.9505;
					double xWhite = 95.047;
					double yWhite = 100.0;
					double zWhite = 108.883;
					double lVal = 116.0 * labFunc(yVal / yWhite) - 16;
					double aVal = 500.0 * (labFunc(xVal / xWhite) - labFunc(yVal / yWhite));
					double bVal = 200.0 * (labFunc(yVal / yWhite) - labFunc(zVal / zWhite));
					colorLabL.setValue(lVal);
					colorLabA.setValue(aVal);
					colorLabB.setValue(bVal);

					colorLabL.valueProperty().addListener(labChangeListener);
					colorLabA.valueProperty().addListener(labChangeListener);
					colorLabB.valueProperty().addListener(labChangeListener);

				} finally {
					inChange = false;
				}
			}
		};

		final ChangeListener<Number> hlsChangeListener = new ChangeListener<Number>() {

			private boolean inChange = false;

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (inChange) {
					return;
				}
				try {
					double colorHlsHVal = colorHlsH.getValue();
					double colorHlsLVal = colorHlsL.getValue();
					double colorHlsSVal = colorHlsS.getValue();

					double cVal = (1 - Math.abs(2 * colorHlsLVal - 1)) * colorHlsSVal;
					double xVal = cVal * (1 - Math.abs((colorHlsHVal / 60.0) % 2 - 1));
					double mVal = colorHlsLVal - cVal / 2.0;

					double rNorm, gNorm, bNorm;
					if (colorHlsHVal < 60) {
						rNorm = cVal;
						gNorm = xVal;
						bNorm = 0;
					} else if (colorHlsHVal < 120) {
						rNorm = xVal;
						gNorm = cVal;
						bNorm = 0;
					} else if (colorHlsHVal < 180) {
						rNorm = 0;
						gNorm = cVal;
						bNorm = xVal;
					} else if (colorHlsHVal < 240) {
						rNorm = 0;
						gNorm = xVal;
						bNorm = cVal;
					} else if (colorHlsHVal < 300) {
						rNorm = xVal;
						gNorm = 0;
						bNorm = cVal;
					} else if (colorHlsHVal < 360) {
						rNorm = cVal;
						gNorm = 0;
						bNorm = xVal;
					} else {
						rNorm = 0;
						gNorm = 0;
						bNorm = 0;
					}
					colorRgbR.setValue((rNorm + mVal) * 255);
					colorRgbG.setValue((gNorm + mVal) * 255);
					colorRgbB.setValue((bNorm + mVal) * 255);

				} finally {
					inChange = false;
				}
			}
		};

		colorRgbR.valueProperty().addListener(rgbChangeListener);
		colorRgbG.valueProperty().addListener(rgbChangeListener);
		colorRgbB.valueProperty().addListener(rgbChangeListener);

		colorCmyC.valueProperty().addListener(cmyToRgb(colorRgbR));
		colorCmyM.valueProperty().addListener(cmyToRgb(colorRgbG));
		colorCmyY.valueProperty().addListener(cmyToRgb(colorRgbB));

		colorHlsH.valueProperty().addListener(hlsChangeListener);
		colorHlsL.valueProperty().addListener(hlsChangeListener);
		colorHlsS.valueProperty().addListener(hlsChangeListener);

		colorLabL.valueProperty().addListener(labChangeListener);
		colorLabA.valueProperty().addListener(labChangeListener);
		colorLabB.valueProperty().addListener(labChangeListener);

		bindLabelWithSlider(labelRGB, "RGB (%.0f; %.0f; %.0f)", colorRgbR, colorRgbG, colorRgbB);
		bindLabelWithSlider(labelCMY, "CMY (%.2f; %.2f; %.2f)", colorCmyC, colorCmyM, colorCmyY);
		bindLabelWithSlider(labelHLS, "HLS (%.0f; %.2f; %.2f)", colorHlsH, colorHlsL, colorHlsS);
		bindLabelWithSlider(labelLab, "L*a*b* (%.0f; %.0f; %.0f)", colorLabL, colorLabA, colorLabB);
	}

	private double labFunc(double t) {
		if (t >= 0.008856) {
			return Math.pow(t, 1.0 / 3.0);
		} else {
			return 7.787 * t + 16.0 / 116.0;
		}
	}

	private void bindLabelWithSlider(Label label, String format, Slider... slider) {
		label.textProperty().bind(StringExpression.stringExpression(
				Bindings.format(format, slider[0].valueProperty(), slider[1].valueProperty(),
						slider[2].valueProperty())));
	}

	private double normalizeRGB(DoubleProperty colorRgbRVal) {
		return colorRgbRVal.doubleValue() / 255;
	}

	private double rgbToCmy(double rgbVal) {
		return 1 - rgbVal;
	}

	private ChangeListener<? super Number> cmyToRgb(Slider rgbSlider) {
		return new ChangeListener<Number>() {

			private boolean inChange = false;

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (inChange) {
					return;
				}
				try {
					inChange = true;
					if (newValue.equals(Double.NaN)) {
						newValue = 0;
					}
					rgbSlider.setValue(255 * (1 - newValue.doubleValue()));
				} finally {
					inChange = false;
				}
			}
		};
	}

}

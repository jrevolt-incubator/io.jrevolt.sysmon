package io.jrevolt.sysmon.client.ui;

import javafx.stage.Stage;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class StageCfg {

	double x, y;
	double width, height;

	public StageCfg() {
	}

	public StageCfg(Stage stage) {
		x = stage.getX();
		y = stage.getY();
		width = stage.getWidth();
		height = stage.getHeight();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}

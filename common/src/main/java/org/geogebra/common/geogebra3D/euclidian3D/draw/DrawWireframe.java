package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.util.DoubleUtil;

public class DrawWireframe {

	// number of intervals in root mesh (for each parameters, if parameters
	// delta are equals)
	private static final short ROOT_MESH_INTERVALS_SPEED = 10;
	private static final short ROOT_MESH_INTERVALS_SPEED_SQUARE = ROOT_MESH_INTERVALS_SPEED
			* ROOT_MESH_INTERVALS_SPEED;

	private final DrawSurface3D drawable;
	private final SurfaceEvaluable geo;

	// corners for drawing wireframe (bottom and right sides)
	private DrawSurface3D.Corner[] wireframeBottomCorners;
	private DrawSurface3D.Corner[] wireframeRightCorners;
	private int wireframeBottomCornersLength;
	private int wireframeRightCornersLength;

	private SurfaceParameter uParam = new SurfaceParameter();
	private SurfaceParameter vParam = new SurfaceParameter();

	private DrawSurface3D.Corner firstCorner;


	public DrawWireframe(DrawSurface3D drawable, SurfaceEvaluable geo) {
		this.drawable = drawable;
		this.geo = geo;
	}

	private static boolean wireframeNeeded() {
		return true;
	}

	private void initParams() throws Exception {
		// calc min/max values
		uParam.initBorder(geo, drawable.getView3D(), 0);
		vParam.initBorder(geo, drawable.getView3D(), 1);
		if (DoubleUtil.isZero(uParam.delta)
				|| DoubleUtil.isZero(vParam.delta)) {
			throw new Exception();
		}

		double uOverVFactor = uParam.delta / vParam.delta;
		if (uOverVFactor > ROOT_MESH_INTERVALS_SPEED) {
			uOverVFactor = ROOT_MESH_INTERVALS_SPEED;
		} else if (uOverVFactor < 1.0 / ROOT_MESH_INTERVALS_SPEED) {
			uOverVFactor = 1.0 / ROOT_MESH_INTERVALS_SPEED;
		}
		uParam.n = (int) (ROOT_MESH_INTERVALS_SPEED * uOverVFactor);
		vParam.n = ROOT_MESH_INTERVALS_SPEED_SQUARE / uParam.n;
		uParam.n += 2;
		vParam.n += 2;

		SurfaceEvaluable.LevelOfDetail detail = geo.getLevelOfDetail();
		uParam.init(detail);
		vParam.init(detail);

//		debug("grids: " + uParam.n + ", " + vParam.n);
	}


	public void initWireframe() throws Exception {
		initParams();

		if (wireframeNeeded()) {
			wireframeBottomCorners = new DrawSurface3D.Corner[uParam.getCornerCount()];
			wireframeRightCorners = new DrawSurface3D.Corner[vParam.getCornerCount()];
		}

		DrawSurface3D.Corner bottomRight = drawable.newCorner(uParam.borderMax, vParam.borderMax);
		DrawSurface3D.Corner first = bottomRight;
		wireframeBottomCornersLength = 0;
		wireframeRightCornersLength = 0;
		int wireFrameSetU = uParam.wireFrameStep,
				wireFrameSetV = vParam.wireFrameStep;
		if (wireframeNeeded()) {
			if (uParam.wireframeUnique) {
				wireFrameSetU = 0;
			}
			if (uParam.wireframeBorder == 1) { // draw edges
				wireframeBottomCorners[0] = first;
				wireframeBottomCornersLength = 1;
				wireFrameSetU = 1;
			}
			if (vParam.wireframeUnique) {
				wireFrameSetV = 0;
			}
			if (vParam.wireframeBorder == 1) { // draw edges
				wireframeRightCorners[0] = first;
				wireframeRightCornersLength = 1;
				wireFrameSetV = 1;
			}
		}

		// first row
		DrawSurface3D.Corner right = bottomRight;
		int uN = uParam.n;
		for (int i = 0; i < uN - 1; i++) {
			right = addLeftToMesh(right, uParam.max - (uParam.delta * i) / uN,
					vParam.borderMax);
			if (wireframeNeeded()) {
				if (wireFrameSetU == uParam.wireFrameStep) { // set wireframe
					wireframeBottomCorners[wireframeBottomCornersLength] = right;
					wireframeBottomCornersLength++;
					if (uParam.wireframeUnique) {
						wireFrameSetU++;
					} else {
						wireFrameSetU = 1;
					}
				} else {
					wireFrameSetU++;
				}
			}
		}
		right = addLeftToMesh(right, uParam.borderMin, vParam.borderMax);
		if (wireframeNeeded()) {
			if (uParam.wireframeBorder == 1) {
				wireframeBottomCorners[wireframeBottomCornersLength] = right;
				wireframeBottomCornersLength++;
			}
		}
		int vN = vParam.n;
		// all intermediate rows
		for (int j = 0; j < vN - 1; j++) {
			bottomRight = addRowAboveToMesh(bottomRight,
					vParam.max - (vParam.delta * j) / vN, uParam.borderMin,
					uParam.borderMax, uParam.max, uN);
			if (wireframeNeeded()) {
				if (wireFrameSetV == vParam.wireFrameStep) { // set wireframe
					wireframeRightCorners[wireframeRightCornersLength] = bottomRight;
					wireframeRightCornersLength++;
					if (vParam.wireframeUnique) {
						wireFrameSetV++;
					} else {
						wireFrameSetV = 1;
					}
				} else {
					wireFrameSetV++;
				}
			}
		}

		// last row
		bottomRight = addRowAboveToMesh(bottomRight, vParam.borderMin,
				uParam.borderMin, uParam.borderMax, uParam.max, uN);
		if (wireframeNeeded()) {
			if (vParam.wireframeBorder == 1) {
				wireframeRightCorners[wireframeRightCornersLength] = bottomRight;
				wireframeRightCornersLength++;
			}
		}

		firstCorner = first;
	}

	public void splitRootMesh() throws DrawSurface3D.NotEnoughCornersException {
		splitRootMesh(firstCorner);
	}


	final private DrawSurface3D.Corner addLeftToMesh(DrawSurface3D.Corner right, double u, double v)
			throws DrawSurface3D.NotEnoughCornersException {
		DrawSurface3D.Corner left = drawable.newCorner(u, v);
		right.l = left;
		return left;
	}

	final private DrawSurface3D.Corner addRowAboveToMesh(DrawSurface3D.Corner bottomRight, double v,
			double uBorderMin, double uBorderMax, double uMax, int uN)
			throws DrawSurface3D.NotEnoughCornersException {
		DrawSurface3D.Corner below = bottomRight;
		DrawSurface3D.Corner right = drawable.newCorner(uBorderMax, v);
		below.a = right;
		for (int i = 0; i < uN - 1; i++) {
			right = addLeftToMesh(right, uMax - (uParam.delta * i) / uN, v);
			below = below.l;
			below.a = right;
		}
		right = addLeftToMesh(right, uBorderMin, v);
		below = below.l;
		below.a = right;

		return bottomRight.a;
	}

	private static void splitRootMesh(DrawSurface3D.Corner first) throws
			DrawSurface3D.NotEnoughCornersException {
		DrawSurface3D.Corner nextAbove, nextLeft;

		DrawSurface3D.Corner current = first;
		while (current.a != null) {
			nextAbove = current.a;
			while (current.l != null) {
				nextLeft = current.l;
				if (nextLeft.a == null) { // already splitted by last row
					nextLeft = nextLeft.l;
				}
				// Log.debug(current.u + "," + current.v);
				current.split(false);
				current = nextLeft;
			}
			current = nextAbove;
		}

	}

	public void drawWireframe(Renderer renderer) {
		if (!wireframeNeeded()) {
			return;
		}
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		int thickness = drawable.getGeoElement().getLineThickness();

		// point were already scaled
		renderer.getGeometryManager().setScalerIdentity();

		drawable.setPackCurve(true);
		brush.start(drawable.getReusableGeometryIndex());
		brush.setThickness(thickness, (float) drawable.getView3D().getScale());
		brush.setAffineTexture(0f, 0f);
		brush.setLength(1f);

//		for (int i = 0; i < wireframeBottomCornersLength; i++) {
//			DrawSurface3D.Corner above = wireframeBottomCorners[i];
//			boolean currentPointIsDefined = isDefinedForWireframe(above);
//			if (currentPointIsDefined) {
//				brush.moveTo(above.p.getXd(), above.p.getYd(), above.p.getZd());
//			}
//			boolean lastPointIsDefined = currentPointIsDefined;
//			above = above.a;
//			while (above != null) {
//				currentPointIsDefined = isDefinedForWireframe(above);
//				if (currentPointIsDefined) {
//					if (lastPointIsDefined) {
//						brush.drawTo(above.p.getXd(), above.p.getYd(),
//								above.p.getZd(), true);
//					} else {
//						brush.moveTo(above.p.getXd(), above.p.getYd(),
//								above.p.getZd());
//					}
//				}
//				lastPointIsDefined = currentPointIsDefined;
//				above = above.a;
//			}
//			brush.endPlot();
//		}

		for (int i = 0; i < wireframeRightCornersLength; i++) {
			DrawSurface3D.Corner left = wireframeRightCorners[i];
			boolean currentPointIsDefined = isDefinedForWireframe(left);
			if (currentPointIsDefined) {
				brush.moveTo(left.p.getXd(), left.p.getYd(), left.p.getZd());
			}
			boolean lastPointIsDefined = currentPointIsDefined;
			left = left.l;
			while (left != null) {
				currentPointIsDefined = isDefinedForWireframe(left);
				if (currentPointIsDefined) {
					if (lastPointIsDefined) {
						brush.drawTo(left.p.getXd(), left.p.getYd(),
								left.p.getZd(), true);
					} else {
						brush.moveTo(left.p.getXd(), left.p.getYd(),
								left.p.getZd());
					}
				}
				lastPointIsDefined = currentPointIsDefined;
				left = left.l;
			}
			brush.endPlot();
		}

		drawable.setGeometryIndex(brush.end());
		drawable.endPacking();

		// point were already scaled
		renderer.getGeometryManager().setScalerView();
	}

	static private boolean isDefinedForWireframe(DrawSurface3D.Corner corner) {
		if (corner.p.isFinalUndefined()) {
			return false;
		}
		return corner.p.isDefined();
	}
}

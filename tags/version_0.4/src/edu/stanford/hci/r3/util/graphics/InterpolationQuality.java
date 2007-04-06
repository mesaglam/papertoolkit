package edu.stanford.hci.r3.util.graphics;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;

public enum InterpolationQuality {
	BICUBIC(new InterpolationBicubic(8)),

	BICUBIC2(new InterpolationBicubic2(8)),

	BILINEAR(new InterpolationBilinear()),

	NEAREST_NEIGHBOR(new InterpolationNearest());

	private Interpolation interpolation;

	private InterpolationQuality(Interpolation interp) {
		interpolation = interp;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}
}

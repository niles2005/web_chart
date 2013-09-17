/**
 * 
 *
 * History:
 *   Aug 17, 2009 5:55:33 PM Created by NieLei
 */
package com.xtwsoft.images;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Aug 17, 2009 5:55:33 PM
 *
 */
public class LZWEncoder {
	private int nBits;

	private int maxbits;

	private int maxcode;

	private int maxmaxcode;

	private int htab[];

	private int codetab[];

	private int hsize;

	private int freeEnt;

	private boolean clearFlg;

	private int gInitBits;

	private int clearCode;

	private int EOFCode;

	private int initBits;

	private ArrayPixelSource source;

	private OutputStream output;

	private int curAccum;

	private int curBits;

	private int masks[] = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047,
			4095, 8191, 16383, 32767, 65535 };

	private byte accum[];

	private int aCount;

	public LZWEncoder(int initBits, ArrayPixelSource source, OutputStream output) {
		maxbits = 12;
		maxmaxcode = 4096;
		htab = new int[5003];
		codetab = new int[5003];
		hsize = 5003;
		freeEnt = 0;
		clearFlg = false;
		curAccum = 0;
		curBits = 0;
		accum = new byte[256];
		this.initBits = initBits;
		this.source = source;
		this.output = output;
	}

	private final int maxCode(int nBits) {
		return (1 << nBits) - 1;
	}

	public void compress() throws IOException {
		gInitBits = initBits;
		clearFlg = false;
		nBits = gInitBits;
		maxcode = maxCode(nBits);
		clearCode = 1 << initBits - 1;
		EOFCode = clearCode + 1;
		freeEnt = clearCode + 2;
		charInit();
		int ent = source.nextPixel();
		int hshift = 0;
		for (int fcode = hsize; fcode < 0x10000; fcode *= 2)
			hshift++;

		hshift = 8 - hshift;
		int hsizeReg = hsize;
		clHash(hsizeReg);
		output(clearCode);
		label0: do {
			int c;
			if ((c = source.nextPixel()) == -1)
				break;
			int fcode = (c << maxbits) + ent;
			int i = c << hshift ^ ent;
			if (htab[i] == fcode) {
				ent = codetab[i];
				continue;
			}
			if (htab[i] >= 0) {
				int disp = hsizeReg - i;
				if (i == 0)
					disp = 1;
				do {
					if ((i -= disp) < 0)
						i += hsizeReg;
					if (htab[i] != fcode)
						continue;
					ent = codetab[i];
					continue label0;
				} while (htab[i] >= 0);
			}
			output(ent);
			ent = c;
			if (freeEnt < maxmaxcode) {
				codetab[i] = freeEnt++;
				htab[i] = fcode;
			} else {
				clBlock();
			}
		} while (true);
		output(ent);
		output(EOFCode);
	}

	private void output(int code) throws IOException {
		curAccum &= masks[curBits];
		if (curBits > 0)
			curAccum |= code << curBits;
		else
			curAccum = code;
		for (curBits += nBits; curBits >= 8; curBits -= 8) {
			charOut((byte) (curAccum & 0xff));
			curAccum >>= 8;
		}

		if (freeEnt > maxcode || clearFlg)
			if (clearFlg) {
				maxcode = maxCode(nBits = gInitBits);
				clearFlg = false;
			} else {
				nBits++;
				if (nBits == maxbits)
					maxcode = maxmaxcode;
				else
					maxcode = maxCode(nBits);
			}
		if (code == EOFCode) {
			for (; curBits > 0; curBits -= 8) {
				charOut((byte) (curAccum & 0xff));
				curAccum >>= 8;
			}

			flushChar();
		}
	}

	private void clBlock() throws IOException {
		clHash(hsize);
		freeEnt = clearCode + 2;
		clearFlg = true;
		output(clearCode);
	}

	private void clHash(int hsize) {
		for (int i = 0; i < hsize; i++)
			htab[i] = -1;

	}

	private void charInit() {
		aCount = 0;
	}

	private void charOut(byte c) throws IOException {
		accum[aCount++] = c;
		if (aCount >= 254)
			flushChar();
	}

	private void flushChar() throws IOException {
		if (aCount > 0) {
			output.write(aCount);
			output.write(accum, 0, aCount);
			aCount = 0;
		}
	}

}
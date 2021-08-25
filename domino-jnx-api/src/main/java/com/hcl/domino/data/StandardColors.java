/*
 * ==========================================================================
 * Copyright (C) 2019-2021 HCL America, Inc. ( http://www.hcl.com/ )
 *                            All rights reserved.
 * ==========================================================================
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may
 * not use this file except in compliance with the License.  You may obtain a
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.
 *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the  specific language  governing permissions  and limitations
 * under the License.
 * ==========================================================================
 */
package com.hcl.domino.data;

import com.hcl.domino.misc.INumberEnum;
import com.hcl.domino.misc.NotesConstants;

/**
 * These symbols are used to specify text color, graphic color and background
 * color in a variety of C API structures.
 *
 * @author Karsten Lehmann
 */
public enum StandardColors implements INumberEnum<Byte> {
  Black(NotesConstants.NOTES_COLOR_BLACK, 0x00, 0x00, 0x00),
  White(NotesConstants.NOTES_COLOR_WHITE, 0xff, 0xff, 0xff),
  Red(NotesConstants.NOTES_COLOR_RED, 0xff, 0x00, 0x00),
  Green(NotesConstants.NOTES_COLOR_GREEN, 0x00, 0xff, 0x00),
  Blue(NotesConstants.NOTES_COLOR_BLUE, 0x00, 0x00, 0xff),
  Magenta(NotesConstants.NOTES_COLOR_MAGENTA, 0xff, 0x00, 0xff),
  Yellow(NotesConstants.NOTES_COLOR_YELLOW, 0xff, 0xff, 0x00),
  Cyan(NotesConstants.NOTES_COLOR_CYAN, 0x00, 0xff, 0xff),
  DarkRed(NotesConstants.NOTES_COLOR_DKRED, 0x80, 0x00, 0x00),
  DarkGreen(NotesConstants.NOTES_COLOR_DKGREEN, 0x00, 0x80, 0x00),
  DarkBlue(NotesConstants.NOTES_COLOR_DKBLUE, 0x00, 0x00, 0x80),
  DarkMagenta(NotesConstants.NOTES_COLOR_DKMAGENTA, 0x80, 0x00, 0x80),
  DarkYellow(NotesConstants.NOTES_COLOR_DKYELLOW, 0x80, 0x80, 0x00),
  DarkCyan(NotesConstants.NOTES_COLOR_DKCYAN, 0x00, 0x80, 0x80),
  Gray(NotesConstants.NOTES_COLOR_GRAY, 0x80, 0x80, 0x80),
  LightGray(NotesConstants.NOTES_COLOR_LTGRAY, 0xc0, 0xc0, 0xc0),
  
  White2(16, 0xff, 0xff, 0xff),
  Vanilla(17, 0xff, 0xef, 0xce),
  Parchment(18, 0xff, 0xff, 0xc2),
  Ivory(19, 0xff, 0xff, 0xd0),
  PaleGreen(20, 0xe0, 0xff, 0xbf),
  SeaMist(21, 0xe0, 0xff, 0xdf),
  IceBlue(22, 0xe0, 0xff, 0xff),
  PowderBlue(23, 0xc2, 0xef, 0xff),
  ArcticBlue(24, 0xe0, 0xf1, 0xff),
  LilacMist(25, 0xe0, 0xe0, 0xff),
  PurpleWash(26, 0xe8, 0xe0, 0xff),
  VioletFrost(27, 0xf1, 0xe0, 0xff),
  Seashell(28, 0xff, 0xe0, 0xff),
  RosePearl(29, 0xff, 0xe0, 0xf5),
  PaleCherry(30, 0xff, 0xe0, 0xe6),
  White3(31, 0xff, 0xff, 0xff),
  Blush(32, 0xff, 0xe1, 0xdc),
  Sand(33, 0xff, 0xe1, 0xb0),
  LightYellow(34, 0xff, 0xff, 0x80),
  Honeydew(35, 0xf1, 0xf1, 0xb4),
  Celery(36, 0xc2, 0xff, 0x91),
  PaleAqua(37, 0xc1, 0xff, 0xd5),
  PaleBlue(38, 0xc1, 0xff, 0xff),
  CrystalBlue(39, 0xa1, 0xe2, 0xff),
  LightCornflower(40, 0xc0, 0xe1, 0xff),
  PaleLavender(41, 0xbf, 0xbf, 0xff),
  GrapeFizz(42, 0xd2, 0xbf, 0xff),
  PalePlum(43, 0xe1, 0xbf, 0xff),
  PalePink(44, 0xff, 0xc1, 0xfd),
  PaleRose(45, 0xff, 0xc0, 0xe4),
  RoseQuartz(46, 0xff, 0xc0, 0xce),
  Gray5(47, 0xf7, 0xf7, 0xf7),
  RedSand(48, 0xff, 0xc0, 0xb6),
  Buff(49, 0xff, 0xc2, 0x81),
  Lemon(50, 0xff, 0xff, 0x35),
  PaleLemonLime(51, 0xf1, 0xf1, 0x80),
  MintGreen(52, 0x80, 0xff, 0x80),
  PastelGreen(53, 0x82, 0xff, 0xca),
  PastelBlue(54, 0x80, 0xff, 0xff),
  Sapphire(55, 0x82, 0xe0, 0xff),
  Cornflower(56, 0x82, 0xc0, 0xff),
  LightLavender(57, 0x9f, 0x9f, 0xff),
  PalePurple(58, 0xc2, 0x9f, 0xff),
  LightOrchid(59, 0xe2, 0x9f, 0xff),
  PinkOrchid(60, 0xff, 0x9f, 0xff),
  AppleBlossom(61, 0xff, 0x9f, 0xcf),
  PinkCoral(62, 0xff, 0x9f, 0xa9),
  Gray10(63, 0xef, 0xef, 0xef),
  LightSalmon(64, 0xff, 0x9f, 0x9f),
  LightPeach(65, 0xff, 0x9f, 0x71),
  Yellow2(66, 0xff, 0xff, 0x00),
  Avocado(67, 0xe0, 0xe0, 0x74),
  LeafGreen(68, 0x41, 0xff, 0x32),
  LightAqua(69, 0x42, 0xff, 0xc7),
  LightTurquoise(70, 0x42, 0xff, 0xff),
  LightCerulean(71, 0x00, 0xbf, 0xff),
  Azure(72, 0x52, 0x91, 0xef),
  Lavender(73, 0x80, 0x80, 0xff),
  LightPurple(74, 0xc0, 0x82, 0xff),
  DustyViolet(75, 0xe0, 0x81, 0xff),
  Pink(76, 0xff, 0x7f, 0xff),
  PastelPink(77, 0xff, 0x82, 0xc2),
  PastelRed(78, 0xff, 0x82, 0xa0),
  Gray15(79, 0xe1, 0xe1, 0xe1),
  Salmon(80, 0xff, 0x80, 0x80),
  Peach(81, 0xff, 0x81, 0x41),
  Mustard(82, 0xff, 0xe1, 0x18),
  LemonLime(83, 0xe1, 0xe1, 0x40),
  NeonGreen(84, 0x00, 0xff, 0x00),
  Aqua(85, 0x00, 0xff, 0xb2),
  Turquoise(86, 0x00, 0xff, 0xff),
  Cerulean(87, 0x00, 0xa1, 0xe0),
  Wedgewood(88, 0x21, 0x81, 0xff),
  Heather(89, 0x61, 0x81, 0xff),
  PurpleHaze(90, 0xa1, 0x60, 0xff),
  Orchid(91, 0xc0, 0x62, 0xff),
  Flamingo(92, 0xff, 0x5f, 0xff),
  CherryPink(93, 0xff, 0x60, 0xaf),
  RedCoral(94, 0xff, 0x60, 0x88),
  Gray20(95, 0xd2, 0xd2, 0xd2),
  DarkSalmon(96, 0xff, 0x40, 0x40),
  DarkPeach(97, 0xff, 0x42, 0x1e),
  Gold(98, 0xff, 0xbf, 0x18),
  YellowGreen(99, 0xe1, 0xe1, 0x00),
  LightGreen(100, 0x00, 0xe1, 0x00),
  Caribbean(101, 0x00, 0xe1, 0xad),
  DarkPastelBlue(102, 0x00, 0xe0, 0xe0),
  DarkCerulean(103, 0x00, 0x82, 0xbf),
  ManganeseBlue(104, 0x00, 0x80, 0xff),
  Lilac(105, 0x41, 0x81, 0xff),
  Purple(106, 0x82, 0x42, 0xff),
  LightRedViolet(107, 0xc1, 0x40, 0xff),
  LightMagenta(108, 0xff, 0x42, 0xf9),
  Rose(109, 0xff, 0x40, 0xa0),
  CarnationPink(110, 0xff, 0x40, 0x70),
  Gray25(111, 0xc0, 0xc0, 0xc0),
  Watermelon(112, 0xff, 0x1f, 0x35),
  Tangerine(113, 0xff, 0x1f, 0x10),
  Orange(114, 0xff, 0x81, 0x00),
  Chartreuse(115, 0xbf, 0xbf, 0x00),
  Green2(116, 0x00, 0xc2, 0x00),
  Teal(117, 0x00, 0xc1, 0x96),
  DarkTurquoise(118, 0x00, 0xc1, 0xc2),
  LightSlateBlue(119, 0x41, 0x81, 0xc0),
  MediumBlue(120, 0x00, 0x62, 0xe1),
  DarkLilac(121, 0x41, 0x41, 0xff),
  RoyalPurple(122, 0x42, 0x00, 0xff),
  Fuchsia(123, 0xc2, 0x00, 0xff),
  ConfettiPink(124, 0xff, 0x22, 0xff),
  PaleBurgundy(125, 0xf5, 0x2b, 0x97),
  Strawberry(126, 0xff, 0x22, 0x59),
  Gray30(127, 0xb2, 0xb2, 0xb2),
  Rouge(128, 0xe0, 0x1f, 0x25),
  BurntOrange(129, 0xe1, 0x20, 0x00),
  DarkOrange(130, 0xe2, 0x62, 0x00),
  LightOlive(131, 0xa1, 0xa1, 0x00),
  KellyGreen(132, 0x00, 0xa0, 0x00),
  SeaGreen(133, 0x00, 0x9f, 0x82),
  AztecBlue(134, 0x00, 0x80, 0x80),
  DustyBlue(135, 0x00, 0x60, 0xa0),
  Blueberry(136, 0x00, 0x41, 0xc2),
  Violet(137, 0x00, 0x21, 0xbf),
  DeepPurple(138, 0x41, 0x00, 0xc2),
  RedViolet(139, 0x81, 0x00, 0xff),
  HotPink(140, 0xff, 0x00, 0xff),
  DarkRose(141, 0xff, 0x00, 0x80),
  PoppyRed(142, 0xff, 0x00, 0x41),
  Gray35(143, 0xa2, 0xa2, 0xa2),
  Crimson(144, 0xc2, 0x00, 0x00),
  Red2(145, 0xff, 0x00, 0x00),
  LightBrown(146, 0xbf, 0x41, 0x00),
  Olive(147, 0x80, 0x80, 0x00),
  DarkGreen2(148, 0x00, 0x80, 0x00),
  DarkTeal(149, 0x00, 0x82, 0x50),
  Spruce(150, 0x00, 0x60, 0x62),
  SlateBlue(151, 0x00, 0x40, 0x80),
  NavyBlue(152, 0x00, 0x1f, 0xe2),
  BlueViolet(153, 0x40, 0x40, 0xc2),
  Amethyst(154, 0x40, 0x00, 0xa2),
  DarkRedViolet(155, 0x60, 0x00, 0xa1),
  Magenta2(156, 0xe0, 0x00, 0xe0),
  LightBurgundy(157, 0xdf, 0x00, 0x7f),
  CherryRed(158, 0xc2, 0x00, 0x41),
  Gray40(159, 0x8f, 0x8f, 0x8f),
  DarkCrimson(160, 0xa0, 0x00, 0x00),
  DarkRed2(161, 0xe1, 0x00, 0x00),
  Hazelnut(162, 0xa1, 0x3f, 0x00),
  DarkOlive(163, 0x62, 0x62, 0x00),
  Emerald(164, 0x00, 0x60, 0x00),
  Malachite(165, 0x00, 0x60, 0x3c),
  DarkSpruce(166, 0x00, 0x40, 0x41),
  SteelBlue(167, 0x00, 0x2f, 0x80),
  Blue2(168, 0x00, 0x00, 0xff),
  Iris(169, 0x20, 0x20, 0xa0),
  Grape(170, 0x22, 0x00, 0xa1),
  Plum(171, 0x40, 0x00, 0x80),
  DarkMagenta2(172, 0xa1, 0x00, 0x9f),
  Burgundy(173, 0xc0, 0x00, 0x7f),
  Cranberry(174, 0x9f, 0x00, 0x0f),
  Gray50(175, 0x80, 0x80, 0x80),
  Mahogany(176, 0x60, 0x00, 0x00),
  Brick(177, 0xc2, 0x12, 0x12),
  DarkBrown(178, 0x82, 0x42, 0x00),
  DeepOlive(179, 0x42, 0x42, 0x00),
  DarkEmerald(180, 0x00, 0x42, 0x00),
  Evergreen(181, 0x00, 0x40, 0x23),
  BalticBlue(182, 0x00, 0x32, 0x3f),
  BlueDenim(183, 0x00, 0x20, 0x60),
  CobaltBlue(184, 0x00, 0x20, 0xc2),
  DarkIris(185, 0x22, 0x22, 0xc0),
  Midnight(186, 0x00, 0x00, 0x80),
  DarkPlum(187, 0x1f, 0x00, 0x7f),
  PlumRed(188, 0x80, 0x00, 0x80),
  DarkBurgundy(189, 0x82, 0x00, 0x40),
  Scarlet(190, 0x80, 0x00, 0x00),
  Gray60(191, 0x5f, 0x5f, 0x5f),
  Chestnut(192, 0x40, 0x00, 0x00),
  TerraCotta(193, 0xa1, 0x1f, 0x12),
  Umber(194, 0x60, 0x42, 0x00),
  Amazon(195, 0x21, 0x21, 0x00),
  PeacockGreen(196, 0x00, 0x21, 0x00),
  Pine(197, 0x00, 0x20, 0x1f),
  SealBlue(198, 0x00, 0x20, 0x41),
  DarkSlateBlue(199, 0x00, 0x20, 0x4f),
  RoyalBlue(200, 0x00, 0x00, 0xe0),
  Lapis(201, 0x00, 0x00, 0xa1),
  DarkGrape(202, 0x00, 0x00, 0x61),
  Aubergine(203, 0x1f, 0x00, 0x62),
  DarkPlumRed(204, 0x40, 0x00, 0x5f),
  Raspberry(205, 0x62, 0x00, 0x42),
  DeepScarlet(206, 0x62, 0x00, 0x12),
  Gray70(207, 0x4f, 0x4f, 0x4f),
  RedGray(208, 0xd0, 0xb1, 0xa1),
  Tan(209, 0xe0, 0xa1, 0x75),
  Khaki(210, 0xd2, 0xb0, 0x6a),
  Putty(211, 0xc0, 0xc2, 0x7c),
  BambooGreen(212, 0x82, 0xc1, 0x68),
  GreenGray(213, 0x81, 0xc0, 0x97),
  BalticGray(214, 0x7f, 0xc2, 0xbc),
  BlueGray(215, 0x71, 0xb2, 0xcf),
  RainCloud(216, 0xb1, 0xb1, 0xd2),
  LilacGray(217, 0x9f, 0x9f, 0xe0),
  LightPurpleGray(218, 0xc0, 0xa1, 0xe0),
  LightMauve(219, 0xe2, 0x9f, 0xde),
  LightPlumGray(220, 0xef, 0x91, 0xeb),
  LightBurgundyGray(221, 0xe2, 0x9f, 0xc8),
  RoseGray(222, 0xf1, 0x8f, 0xbc),
  Gray80(223, 0x2f, 0x2f, 0x2f),
  DarkRedGray(224, 0x7f, 0x60, 0x4f),
  DarkTan(225, 0xa1, 0x62, 0x52),
  Safari(226, 0x80, 0x62, 0x10),
  OliveGray(227, 0x82, 0x82, 0x3f),
  Jade(228, 0x3f, 0x62, 0x1f),
  DarkGreenGray(229, 0x3c, 0x61, 0x3e),
  SpruceGray(230, 0x37, 0x60, 0x5e),
  DarkBlueGray(231, 0x10, 0x41, 0x60),
  AtlanticGray(232, 0x42, 0x42, 0x82),
  DarkLilacGray(233, 0x62, 0x60, 0xa1),
  PurpleGray(234, 0x62, 0x41, 0x81),
  Mauve(235, 0x60, 0x31, 0x81),
  PlumGray(236, 0x60, 0x21, 0x62),
  BurgundyGray(237, 0x62, 0x21, 0x52),
  DarkRoseGray(238, 0x81, 0x3f, 0x62),
  Black2(239, 0x00, 0x00, 0x00);

  private final byte m_color;
  private final short red;
  private final short green;
  private final short blue;

  StandardColors(final int colorIdx, int red, int green, int blue) {
    this.m_color = (byte) (colorIdx);
    this.red = (short)red;
    this.green = (short)green;
    this.blue = (short)blue;
  }

  @Override
  public long getLongValue() {
    return Byte.toUnsignedLong(this.m_color);
  }

  @Override
  public Byte getValue() {
    return this.m_color;
  }
  
  public short getGreen() {
    return green;
  }
  
  public short getRed() {
    return red;
  }
  
  public short getBlue() {
    return blue;
  }
}
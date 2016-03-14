/******************************************************************
 * @(#) DeColores.java      1.3
 * 
 * Copyright (c) 2005, John Miller
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   
 * 1. Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer. 
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials
 *    provided with the distribution. 
 * 3. Neither the name of the University of Georgia nor the names
 *    of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @version     1.3, 28 Jan 2005
 * @author      John Miller
 */
 

package jsim.util;

import java.awt.Color;


/******************************************************************
 * A convenience class defining numerous common colors.
 * Source of colors:  http://www.webmoments.com/colorchart.htm
 */

public class DeColores
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final Color  antiquewhite     = new Color (0xFAEBD7);
    public static final Color  aqua             = new Color (0x00FFFF);
    public static final Color  aquamarine       = new Color (0x7FFFD4);
    public static final Color  azure            = new Color (0xF0FFFF);
    public static final Color  beige            = new Color (0xF5F5DC);
    public static final Color  bisque           = new Color (0xFFE4C4);
    public static final Color  black            = new Color (0x000000);
    public static final Color  blanchedalmond   = new Color (0xFFEBCD);
    public static final Color  blue             = new Color (0x0000FF);
    public static final Color  blueviolet       = new Color (0x8A2BE2);
    public static final Color  brightskyblue    = new Color (0xF0F9FF);
    public static final Color  brown            = new Color (0xA52A2A);
    public static final Color  burlywood        = new Color (0xDEB887);
    public static final Color  cadetblue        = new Color (0x5F9EA0);
    public static final Color  chartreuse       = new Color (0x7FFF00);
    public static final Color  chocolate        = new Color (0xD2691E);
    public static final Color  coral            = new Color (0xFF7F50);
    public static final Color  cornflowerblue   = new Color (0x6495ED);
    public static final Color  cornsilk         = new Color (0xFFF8DC);
    public static final Color  crimson          = new Color (0xDC143C);
    public static final Color  cyan             = new Color (0x00FFFF);
    public static final Color  darkblue         = new Color (0x00008B);
    public static final Color  darkcyan         = new Color (0x008B8B);
    public static final Color  darkgoldenrod    = new Color (0xB8860B);
    public static final Color  darkgray         = new Color (0xA9A9A9);
    public static final Color  darkgreen        = new Color (0x006400);
    public static final Color  darkkhaki        = new Color (0xBDB76B);
    public static final Color  darkmagenta      = new Color (0x8B008B);
    public static final Color  darkolivegreen   = new Color (0x556B2F);
    public static final Color  darkorange       = new Color (0xFF8C00);
    public static final Color  darkorchid       = new Color (0x9932CC);
    public static final Color  darkred          = new Color (0x8B0000);
    public static final Color  darksalmon       = new Color (0xE9967A);
    public static final Color  darkseagreen     = new Color (0x8FBC8F);
    public static final Color  darkslateblue    = new Color (0x483D8B);
    public static final Color  darkslategray    = new Color (0x2F4F4F);
    public static final Color  darkturquoise    = new Color (0x00CED1);
    public static final Color  darkviolet       = new Color (0x9400D3);
    public static final Color  darkyellow       = new Color (0xEEEE00);
    public static final Color  deeppink         = new Color (0xFF1493);
    public static final Color  deepskyblue      = new Color (0x00BFFF);
    public static final Color  dimgray          = new Color (0x696969);
    public static final Color  dodgerblue       = new Color (0x1E90FF);
    public static final Color  firebrick        = new Color (0xB22222);
    public static final Color  floralwhite      = new Color (0xFFFAF0);
    public static final Color  forestgreen      = new Color (0x228B22);
    public static final Color  fuchsia          = new Color (0xFF00F0);
    public static final Color  gainsboro        = new Color (0xDCDCDC);
    public static final Color  ghostwhite       = new Color (0xF8F8FF);
    public static final Color  gold             = new Color (0xFFD700);
    public static final Color  goldenrod        = new Color (0xDAA520);
    public static final Color  gray             = new Color (0x808080);
    public static final Color  green            = new Color (0x008000);
    public static final Color  greenyellow      = new Color (0xADFF2F);
    public static final Color  honeydew         = new Color (0xF0FFF0);
    public static final Color  hotpink          = new Color (0xFF69B4);
    public static final Color  indianred        = new Color (0xCD5C5C);
    public static final Color  indigo           = new Color (0x4B0082);
    public static final Color  ivory            = new Color (0xFFFFF0);
    public static final Color  khaki            = new Color (0xF0E68C);
    public static final Color  lavender         = new Color (0xE6E6FA);
    public static final Color  lavenderblush    = new Color (0xFFF0F5);
    public static final Color  lawngreen        = new Color (0x7CFC00);
    public static final Color  lemonchiffon     = new Color (0xFFFACD);
    public static final Color  lemonlime        = new Color (0xBACD22);
    public static final Color  lightblue        = new Color (0xADD8E6);
    public static final Color  lightcoral       = new Color (0xF08080);
    public static final Color  lightcyan        = new Color (0xE0FFFF);
    public static final Color ltgoldenrodyellow = new Color (0xFAFAD2);
    public static final Color  lightgreen       = new Color (0x90EE90);
    public static final Color  lightgrey        = new Color (0xD3D3D3);
    public static final Color  lightpink        = new Color (0xFFB6C1);
    public static final Color  lightsalmon      = new Color (0xFFA07A);
    public static final Color  lightseagreen    = new Color (0x20B2AA);
    public static final Color  lightskyblue     = new Color (0x87CEFA);
    public static final Color  lightslategray   = new Color (0x778899);
    public static final Color  lightsteelblue   = new Color (0xB0C4DE);
    public static final Color  lightyellow      = new Color (0xFFFFE0);
    public static final Color  lime             = new Color (0x00FF00);
    public static final Color  limegreen        = new Color (0x32CD32);
    public static final Color  linen            = new Color (0xFAF0E6);
    public static final Color  magenta          = new Color (0xFF00FF);
    public static final Color  maroon           = new Color (0x800000);
    public static final Color  mediumaquamarine = new Color (0x66CDAA);
    public static final Color  mediumblue       = new Color (0x0000CD);
    public static final Color  mediumorchid     = new Color (0xBA55D3);
    public static final Color  mediumpurple     = new Color (0x9370DB);
    public static final Color  mediumseagreen   = new Color (0x3CB371);
    public static final Color  mediumslateblue  = new Color (0x7B68EE);
    public static final Color mediumspringgreen = new Color (0x00FA9A);
    public static final Color  mediumturquoise  = new Color (0x48D1CC);
    public static final Color  mediumvioletred  = new Color (0xC71585);
    public static final Color  midnightblue     = new Color (0x191970);
    public static final Color  mintcream        = new Color (0xF5FFFA);
    public static final Color  mistyrose        = new Color (0xFFE4E1);
    public static final Color  moccasin         = new Color (0xFFE4B5);
    public static final Color  navajowhite      = new Color (0xFFDEAD);
    public static final Color  navy             = new Color (0x000080);
    public static final Color  oldlace          = new Color (0xFDF5E6);
    public static final Color  olive            = new Color (0x808000);
    public static final Color  olivedrab        = new Color (0x6B8E23);
    public static final Color  orange           = new Color (0xFFA500);
    public static final Color  orangered        = new Color (0xFF4500);
    public static final Color  orchid           = new Color (0xDA70D6);
    public static final Color  palegoldenrod    = new Color (0xEEE8AA);
    public static final Color  palegreen        = new Color (0x98FB98);
    public static final Color  paleturquoise    = new Color (0xAFEEEE);
    public static final Color  palevioletred    = new Color (0xDB7093);
    public static final Color  papayawhip       = new Color (0xFFEFD5);
    public static final Color  peachpuff        = new Color (0xFFDAB9);
    public static final Color  peru             = new Color (0xCD853F);
    public static final Color  pink             = new Color (0xFFC0CB);
    public static final Color  plum             = new Color (0xDDA0DD);
    public static final Color  powderblue       = new Color (0xB0E0E6);
    public static final Color  purple           = new Color (0x800080);
    public static final Color  red              = new Color (0xFF0000);
    public static final Color  rosybrown        = new Color (0xBC8F8F);
    public static final Color  royalblue        = new Color (0x4169E1);
    public static final Color  saddlebrown      = new Color (0x8B4513);
    public static final Color  salmon           = new Color (0xFA8072);
    public static final Color  sandybrown       = new Color (0xF4A460);
    public static final Color  seagreen         = new Color (0x2E8B57);
    public static final Color  seashell         = new Color (0xFFF5EE);
    public static final Color  sienna           = new Color (0xA0522D);
    public static final Color  silver           = new Color (0xC0C0C0);
    public static final Color  skyblue          = new Color (0x87CEEB);
    public static final Color  slateblue        = new Color (0x6A5ACD);
    public static final Color  slategray        = new Color (0x708090);
    public static final Color  snow             = new Color (0xFFFAFA);
    public static final Color  springgreen      = new Color (0x00FF7F);
    public static final Color  steelblue        = new Color (0x4682B4);
    public static final Color  tan              = new Color (0xD2B48C);
    public static final Color  teal             = new Color (0x008080);
    public static final Color  thistle          = new Color (0xD8BFD8);
    public static final Color  tomato           = new Color (0xFF6347);
    public static final Color  turquoise        = new Color (0x40E0D0);
    public static final Color  violet           = new Color (0xEE82EE);
    public static final Color  wheat            = new Color (0xF5DEB3);
    public static final Color  white            = new Color (0xFFFFFF);
    public static final Color  whitesmoke       = new Color (0xF5F5F5);
    public static final Color  yellow           = new Color (0xFFFF00);
    public static final Color  yellowgreen      = new Color (0x9ACD32);


}; // class


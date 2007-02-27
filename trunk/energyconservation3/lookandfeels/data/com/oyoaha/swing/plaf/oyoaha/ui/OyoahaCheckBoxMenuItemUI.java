/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI
{
  protected String acceleratorDelimiter;

  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaCheckBoxMenuItemUI();
  }

  protected void installDefaults()
  {
    super.installDefaults();
    acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
  }

  public void installUI(JComponent c)
  {
    super.installUI(c);
    OyoahaUtilities.installRolloverListener(c);
  }

  public void uninstallUI(JComponent c)
  {
    super.uninstallUI(c);
    OyoahaUtilities.uninstallRolloverListener(c);
  }

  protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon,int defaultTextIconGap)
  {
    return OyoahaPaintMenu.getOyoahaPaintMenu().getPreferredMenuItemSize(c, checkIcon, arrowIcon, defaultTextIconGap, acceleratorDelimiter, acceleratorFont);
  }

  public void update(Graphics g, JComponent c)
  {
    OyoahaUtilities.paintBackground(g, c);
    paint(g, c);
  }

  protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, Icon arrowIcon, Color background, Color foreground, int defaultTextIconGap)
  {
    OyoahaPaintMenu.getOyoahaPaintMenu().paintMenuItem(g, c, checkIcon, arrowIcon, background, foreground, defaultTextIconGap, acceleratorDelimiter, acceleratorFont, disabledForeground, acceleratorForeground, acceleratorSelectionForeground);
  }
}
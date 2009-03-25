/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author vegar
 */
class Fullscreen
{
  //////////////// Variables /////////////////////////

  boolean fullscreen = false;
  boolean displayChanged = false;

  GraphicsEnvironment ge=null;
  GraphicsDevice gd=null;
  GraphicsDevice myDevice;
  public DisplayMode dm, dm_old;

  ///////////////// Functions /////////////////////////

  public Fullscreen()
      {
	ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	gd = ge.getDefaultScreenDevice();

	// Save old displaymode and get new one to play with.
	dm_old = gd.getDisplayMode();
	dm = dm_old;
      }

  public boolean init( Frame frame )
    {
      frame.setUndecorated( true );
      if( gd.isFullScreenSupported() )
      {
	System.out.println("Fullscreen...");//ddd
	try {
	      gd.setFullScreenWindow( frame );
	      fullscreen = true;
	    } catch( Exception e ) {
	      gd.setFullScreenWindow( null );
	      fullscreen = false;
	    }
	// Once an application is in full-screen exclusive mode,
	// it may be able to take advantage of actively setting the display mode.
	if( fullscreen &&
	    gd.isDisplayChangeSupported() )
	{
	  // Change displaymode here [..]
	  try {
	    myDevice.setDisplayMode( dm );
	    displayChanged = true;
	  } catch( Exception e ) {
	    myDevice.setDisplayMode( dm_old );
	    displayChanged = false;
	  }
	}
      }
      return fullscreen;
    }

  public void exit()
    {
      if (fullscreen) {
	GraphicsEnvironment ge=null;
	GraphicsDevice gd=null;
	ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	gd = ge.getDefaultScreenDevice();
	if( gd.isFullScreenSupported() )
	{
	  gd.setFullScreenWindow(null);
	  System.out.println("Exit fullscreen done.");//ddd
	  if( displayChanged ) {
	    myDevice.setDisplayMode( dm_old );
	  }
	  fullscreen = false;
	}
      }
    }

  public int getHeight()
    {
      //System.out.println("dm.getHeight:"+dm.getHeight());//ddd
      return dm.getHeight();
    }

  public int getWidth()
    {
      //System.out.println("dm.getWidth:"+dm.getWidth());//ddd
      return dm.getWidth();
    }
}


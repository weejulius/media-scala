package com.thenetcircle.services.media.service.impl.features;


import com.thenetcircle.services.common.Location;
import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.ImageStorage;
import com.thenetcircle.services.media.service.impl.area.Area.AreaBuilder;
import com.thenetcircle.services.media.service.impl.location.LocalLocation;
import com.thenetcircle.services.media.service.impl.location.RemoteLocation;
import com.thenetcircle.services.media.service.impl.location.RemoteLocation.Status;
import com.thenetcircle.services.media.service.impl.storage.DefaultImageStorage;
import com.thenetcircle.services.media.service.impl.transform.Images;
import com.thenetcircle.services.media.service.impl.transform.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.matchers.JUnitMatchers;
import processing.core.PImage;
import com.thenetcircle.services.media.service.impl.transform.processing.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;


public class ImageProcessSteps
{
  private Location folderOfProcessedImage;
  private Location processedImageLocation;
  private Location rootLocation=
    new LocalLocation(new File(this.getClass().getResource("/").getFile()).toPath());
  private String errorMessage;
  private Image processingImage;
  private String inputImagePath;
  private ImageStorage imageStorage =new DefaultImageStorage();
  private List<Transform> transforms;


  @Given("^an image processor")
  public void anImageProcessor()
  {
    transforms=new ArrayList<Transform>();
  }

  @Given("^an image is at \"([^\"]*)\"$")
  public void anImageIsAt(final String aImageLocation) throws Exception
  {
    inputImagePath=aImageLocation;
    if(!inputImagePath.startsWith("http"))
    {
      inputImagePath=rootLocation.resolve(inputImagePath).toString();
    }
    processingImage=Images.load(Locations.get(inputImagePath));
  }

  @When("^the directory to place processed image is at \"([^\"]*)\"$")
  public void theDirectoryToPlaceProcessedImageIs(final String processedImageFolder)
    throws IOException
  {
    folderOfProcessedImage=createDirectoryToStoreResult(processedImageFolder);

  }

  @When("^the remote directory to place processed image is at \"([^\"]*)\"$")
  public void theRemoteDirectoryToPlaceProcessedImageIs(final String url)
    throws IOException
  {
    folderOfProcessedImage=new RemoteLocation(new URL(url));
  }


  @When("^I crop the image at \\((-?[.\\d]+),(-?[.\\d]+)\\) to the size \\((-?[.\\d]+),(-?[.\\d]+)\\)$")
  public void iCropTheImageToTheSize(
    final float fromX, final float fromY, final float toWidth, final float toHeight)
    throws IOException
  {
    AreaBuilder builder=new AreaBuilder();
    builder.paddingBuillder.left(fromX).top(fromY);
    builder.sizeBuilder.width(toWidth).height(toHeight);
    transforms.add(new CropTransform(builder));
  }

  @When("^I crop the image at \\((-?\\d+),(-?\\d+)\\)$")
  public void iCropTheImageAt(final int a, final int b) throws IOException
  {
    AreaBuilder builder=new AreaBuilder();

    if(a<0||b<0)
    {
      builder.paddingBuillder.padding(0, 0, Math.abs(a), Math.abs(b));
    }
    else
    {
      builder.paddingBuillder.padding(Math.abs(a), Math.abs(b), 0, 0);
    }


    transforms.add(new CropTransform(builder));
  }


  @When("^I scale the image to the size \\((-?\\d*),(-?\\d*)\\)$")
  public void iScaleTheImageToTheSize(final String newWidth, final String newHieght)
    throws IOException
  {
    try
    {
      AreaBuilder builder=new AreaBuilder();
      if(!newWidth.isEmpty())
      {
        builder.sizeBuilder.width(Integer.parseInt(newWidth));

      }
      if(!newHieght.isEmpty())
      {
        builder.sizeBuilder.height(Integer.parseInt(newHieght));
      }

      transforms.add(new ScaleTransform(builder));
    }
    catch(Exception e)
    {
      errorMessage=e.getMessage();
    }


  }
  @When("^I scale the image by the percentage (\\d+)%")
  public void iScaleTheImageBy(final float percentage) throws IOException
  {
    AreaBuilder builder=new AreaBuilder();
    builder.sizeBuilder.width(percentage*0.01f);
    transforms.add(new ScaleTransform(builder));
  }

  @When("^I overlay text \"([^\"]*)\" at \\((-?\\d+),(-?\\d+)\\)$")
  public void iOverlayText(
    final String text, final int a, final int b)
    throws IOException
  {
    AreaBuilder builder=new AreaBuilder();

    if(a<0||b<0)
    {
      builder.paddingBuillder.right(Math.abs(a)).bottom(Math.abs(b));
    }
    else
    {
      builder.paddingBuillder.left(Math.abs(a)).top(Math.abs(b));
    }

    transforms.add(new OverlayTextTransform(builder, text));
  }

  @When("^I overlay image using \"([^\"]*)\" at \\((-?\\d+),(-?\\d+)\\) with opacity (.+)$")
  public void iOverlayImageUsingAt(final String overlayImage, final int x, final int y,
    final float opacity)
    throws Exception
  {

    AreaBuilder builder=new AreaBuilder();
    if(x<0&&y<0)
    {
      builder.paddingBuillder.right(Math.abs(x)).bottom(Math.abs(y));
    }
    if(x>0&&y>0)
    {
      builder.paddingBuillder.left(Math.abs(x)).top(Math.abs(y));
    }
    transforms.add(new OverlayImageTransform(builder,
      Images.load(rootLocation.resolve(overlayImage)), opacity));
  }

  @When("^I change the format to \"([^\"]*)\"$")
  public void iChangeTheFormatTo(final String newFormat) throws IOException
  {

    processedImageLocation=folderOfProcessedImage.resolve(replaceFormat(
      processingImage.name(),
      newFormat));
    imageStorage.store(
      processingImage, processedImageLocation);

  }

  @When("^I save the image as \"([^\"]*)\"$")
  public void iSaveTheImageAs(final String imageName) throws Exception
  {
    processingImage=Images.load(Locations.get(inputImagePath));
    try
    {
      for(Transform transform : transforms)
      {
        processingImage=transform.transform(processingImage);
      }

      save(imageName);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      errorMessage=e.getMessage();
    }finally{
      transforms=new ArrayList<Transform>();
    }
    
  }

  @Then("I should see an image is at \"([^\"]*)\"$")
  public void iShouldSeeAnImageIsAt(final String resource) throws MalformedURLException
  {
    Location expectedImageLocation=null;
    if(!resource.startsWith("http"))
    {
      expectedImageLocation=rootLocation.resolve(resource);
    }
    else
    {
      expectedImageLocation=new RemoteLocation(new URL(resource));
    }
    Assert.assertEquals(Status.Yes, expectedImageLocation.isExist());
  }

  @Then("^the size of the image is (\\d+),(\\d+)$")
  public void theSizeOfTheImageIs(final int width, final int height) throws IOException
  {
    Assert.assertEquals(width, ((PImage)processingImage.get()).width);
    Assert.assertEquals(height, ((PImage)processingImage.get()).height);
  }

  @Then("^the size of image after dispatch is (\\d+),(\\d+)$")
  public void theSizeOfImageAfterProcessIs(final int width, final int height) throws IOException
  {
    BufferedImage processedImage=ImageIO.read(processedImageLocation.read());
    Assert.assertEquals(width, processedImage.getWidth());
    Assert.assertEquals(height, processedImage.getHeight());

  }

  @Then("^I should see an error message \"([^\"]*)\"$")
  public void iShouldSeeAnErrorMessage(final String message)
  {
    assertThat(errorMessage, containsString(message));
  }

  @Then("^the file format is \"([^\"]*)\"$")
  public void theFileFormatIs(final String format) throws Exception
  {
    Assert.assertThat(
      URLConnection.guessContentTypeFromStream(processedImageLocation.read()),
      JUnitMatchers.containsString(format
        ));
  }
  private Location createDirectoryToStoreResult(final String path) throws IOException
  {

    Location resultDirectory=rootLocation.resolve(path);

    if(resultDirectory.isExist()==Status.Yes)
    {
      resultDirectory.delete();
    }

    resultDirectory.mkdirs();

    return resultDirectory;
  }


  private String replaceFormat(final String name, final String newFormat)
  {
    String currentName=name;
    int startIndexOfExtension=currentName.lastIndexOf('.');
    return currentName.substring(0, startIndexOfExtension+1)+
      newFormat;
  }

  private void save(final String aName) throws IOException
  {
    String name=aName;

    if(name==null)
    {
      name=processingImage.name();
    }
    processedImageLocation=folderOfProcessedImage.resolve(name);
    imageStorage.store(processingImage, processedImageLocation);
  }
}

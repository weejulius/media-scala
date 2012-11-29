Feature: overlay images

    Background:
       Given an image processor
      
       
    Scenario: overlay image
       Given an image is at "samples/sample_300_300.jpeg"
       And the directory to place processed image is at "process_result/overlay_image"
       When I overlay image using "samples/sample_40_40.png" at (20,240) with opacity 0.7
       And I save the image as "sample_300_300_20_240.jpeg"
       Then I should see an image is at "process_result/overlay_image/sample_300_300_20_240.jpeg"
Feature: crop images
    
   Background:
     Given an image processor

   Scenario: crop jpeg     
     Given an image is at "samples/sample_80_80.jpeg"
     And the size of the image is 80,80     
     And the directory to place processed image is at "process_result/crop"
     When I crop the image at (10,10) to the size (40,40)
     And I save the image as "sample_80_80_10_10.jpeg"
     And the size of image after process is 40,40
     
     When I crop the image at (70,10)
     And I save the image as "sample_80_80_70_10.jpeg"
     Then the size of image after process is 10,70
     
   Scenario: percentage padding
     Given an image is at "samples/sample_80_80.jpeg"
     And the size of the image is 80,80     
     And the directory to place processed image is at "process_result/crop"
     When I crop the image at (0.1,0.1) to the size (0.5,0.5)
     And I save the image as "sample_80_80_8_8.jpeg"
     And the size of image after process is 40,40 
     
   Scenario: invalid parameters
     Given an image is at "samples/sample_80_80.jpeg"
     And the directory to place processed image is at "process_result/invalid_crop"  
     When I crop the image at (80,10)
     And I save the image as "sample_80_80_80_10.jpeg"
     Then I should see an error message "invalid size :(0*70)"

     When I crop the image at (30,80)
     And I save the image as "sample_80_80_30_80.jpeg"
     Then I should see an error message "invalid size :(50*0)"
     
     When I crop the image at (-90,-10)
     And I save the image as "sample_80_80_N90_N10.jpeg"
     Then I should see an error message "there is no enough area to move up point"
     
   Scenario: crop png
     Given an image is at "samples/sample_100_100.png"
     And the size of the image is 100,100
     And the directory to place processed image is at "process_result/crop_png"
     When I crop the image at (10,10) to the size (40,40)
     And I save the image as "sample_100_100_10_10.png"
     Then the size of image after process is 40,40
  
     Given an image is at "samples/sample_100_100.png"
     When I crop the image at (70,10)
     And I save the image as "sample_100_100_70_10.png" 
     Then the size of image after process is 30,90
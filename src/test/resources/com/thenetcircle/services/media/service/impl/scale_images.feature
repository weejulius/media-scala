Feature: scale images

  Background:
     Given an image processor
     
  Scenario: scale jpeg     
     Given an image is at "samples/sample_80_80.jpeg"
     And the size of the image is 80,80     
     And the directory to place processed image is at "process_result/scale_jpeg"      
     When I scale the image to the size (40,40)
     And I save the image as "sample_80_80_40_40.jpeg"
     Then the size of image after process is 40,40
  
     When I scale the image to the size (70,10)
     And I save the image as "sample_80_80_70_10.jpeg"
     Then the size of image after process is 70,10
  
  Scenario: keep ratio if either is 0
     Given an image is at "samples/sample_80_80.jpeg"     
     And the directory to place processed image is at "process_result/scale_by_ratio"
     When I scale the image to the size (70,)
     And I save the image as "sample_80_80_70_null.jpeg"
     Then the size of image after process is 70,70

     When I scale the image to the size (,50)
     And I save the image as "sample_80_80_null_50.jpeg"
     Then the size of image after process is 50,50
     
     Given an image is at "samples/sample_45_70.jpeg"
     And I scale the image to the size (25,) 
     And I save the image as "sample_45_70_25_null.jpeg"    
     Then the size of image after process is 25,39
     
  Scenario: invalid parameters 
     Given an image is at "samples/sample_80_80.jpeg"
     And the directory to place processed image is at "process_result/invalid_scale"  
     When I scale the image to the size (-14,10)
     And I save the image as "sample_80_80_N14_10.jpeg"  
     Then I should see an error message "invalid width of size -14.0"
     When I scale the image to the size (0,0)
     And I save the image as "sample_80_80_0_0.jpeg"  
     Then I should see an error message "invalid width of size 0"     
     
  Scenario: scale by percentage
     Given an image is at "samples/sample_80_80.jpeg"     
     And the directory to place processed image is at "process_result/scale_by_percentage"   
     When I scale the image by the percentage 50%
     And I save the image as "sample_80_80_50%.jpeg"
     Then the size of image after process is 40,40
     
  Scenario: scale png
     Given an image is at "samples/sample_80_80.png"
     And the size of the image is 80,80
     And the directory to place processed image is at "process_result/scale_png" 
     When I scale the image to the size (40,40)
     And I save the image as "sample_80_80_50%.png"
     Then the size of image after process is 40,40
  
     When I scale the image to the size (70,10)
     And I save the image as "sample_80_80_70_10.png"
     Then the size of image after process is 70,10
     
     When I scale the image to the size (70,)
     And I save the image as "sample_80_80_70_0.png"
     Then the size of image after process is 70,70
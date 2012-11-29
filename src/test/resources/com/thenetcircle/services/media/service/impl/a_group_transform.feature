Feature: a group transforms
    
   Background:
     Given an image processor
     And the directory to place processed image is at "process_result/group_transform"

   Scenario: crop >> overlay >> format     
     Given an image is at "samples/sample_400_300.png"
     And the size of the image is 400,300
     When I crop the image at (100,100)
     And I overlay text "The.Net.Circle.Inc" at (-30,-20)
     And I change the format to "jpeg"
     And I save the image as "sample_400_300_group.jpeg"
     Then I should see an image is at "process_result/group_transform/sample_400_300_group.jpeg"
     And the size of image after process is 300,200
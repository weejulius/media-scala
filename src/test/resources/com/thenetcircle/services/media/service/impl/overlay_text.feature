Feature: overlay text

    Background:
       Given an image processor
       
    Scenario: overlay text
       Given an image is at "samples/sample_300_300.jpeg"
       And the directory to place processed image is at "process_result/overlay"
       When I overlay text "The netcircle@ 2012. Inc" at (20,260)
       And I save the image as "sample_300_300_20_206.jpeg"
       Then I should see an image is at "process_result/overlay/sample_300_300_20_206.jpeg"
       
       Given an image is at "samples/sample_640_480.jpeg"
       When I overlay text "The netcircle@ 2012. Inc" at (50,300)
       And I save the image as "sample_640_480_50_300.jpeg"
       Then I should see an image is at "process_result/overlay/sample_640_480_50_300.jpeg"       
       
       
    Scenario: overlay text relative to the right bottom corner
       Given an image is at "samples/sample_640_480.jpeg"
       And the directory to place processed image is at "process_result/overlay_relative"
       When I overlay text "Oh my god" at (-10,-30)
       And I save the image as "sample_640_480_N10N30.jpeg"
       Then I should see an image is at "process_result/overlay_relative/sample_640_480_N10N30.jpeg"
       
       Given an image is at "samples/sample_1024_1024.jpeg"
       When I overlay text "The netcircle@ 2012. Inc" at (-100,-100)
       And I save the image as "sample_1024_1024_N100N100.jpeg"
       Then I should see an image is at "process_result/overlay_relative/sample_1024_1024_N100N100.jpeg"
       
    Scenario: invalid parameters
       Given an image is at "samples/sample_640_480.jpeg"
       And the directory to place processed image is at "process_result/invalid_overlay"
       When I overlay text "Oh my god" at (650,-30)
       And I save the image as "sample_640_480.jpeg"
       Then I should see an error message "there is no enough area to move up"
       
       When I overlay text "Oh my god" at (600,-30)
       And I save the image as "sample_640_480.jpeg"
       Then I should see an error message "there is no enough area to move up"
       
       When I overlay text "Oh my god" at (10,490)
       And I save the image as "sample_640_480.jpeg"
       Then I should see an error message "there is no enough area to move down"
       
       When I overlay text "Oh my god" at (-600,-300)
       And I save the image as "sample_640_480.jpeg"
       Then I should see an error message "there is no enough area to move up"
       
   Scenario: overlay text on png    
       Given an image is at "samples/sample_256_256.png"
       And the directory to place processed image is at "process_result/overlay_png"
       When I overlay text "The netcircle@ 2012. Inc" at (-1,-1)
       And I save the image as "sample_256_256_N1N1.png"
       Then I should see an image is at "process_result/overlay_png/sample_256_256_N1N1.png"
       
       Given an image is at "samples/sample_400_300.png"
       When I overlay text "The netcircle@ 2012. Inc" at (-1,-1)
       And I save the image as "sample_400_300N1N1.png"
       Then I should see an image is at "process_result/overlay_png/sample_400_300N1N1.png"
        
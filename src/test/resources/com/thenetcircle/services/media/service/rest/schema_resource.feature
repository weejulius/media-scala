Feature: manipulate the schema resource   
     
     Scenario: deleting no-existing schema
       Given the schema "schema1" is not existing
       And the URL is "media/rest/schemas/schema1"
       When I send request by delete
       Then I should see the response code 404
        
     Scenario: display not existing schema
       Given the schema "schema1" is not existing
       And the URL is "media/rest/schemas/schema1"
       When I send request by get
       Then I should see the response code 404
        
     Scenario: save schema
        Given the schema "schema1" is not existing
        And the URL is "media/rest/schemas"
        And I input a schema
        """
           {
             schema:"schema1",
             transforms:[
                {
                      transform:"crop",
                      padding:[10,20,40,60]
                }
             ],
             options:{
                   quality:0.75
             }
          }
        """
        
        When I send request by post
        Then I should see the response code 204
        
     Scenario: already existing when saving schema
         
         Given the URL is "media/rest/schemas"
         And I input a schema
          """
             {
               schema:"schema1",
               transforms:[
                  {
                        transform:"crop",
                        padding:[10,20,40,60]
                  }
               ],
               options:{
                     quality:0.75
               }
            }
          """ 
         When I send request by post
         Then I should see the response code 403
         And I should see the message:
         """
         {
           "event" : "create a new schema",
           "type" : "CreateExistingEntity",
           "cause" : "validating schema however the schema schema1 is already existing"
         """
     
    
           
     Scenario: the schema name is not same as the name in the schema when updating
        Given the URL is "media/rest/schemas/schema1"
        And I input a schema
          """
             {
               schema:"schema2",
               transforms:[
                  {
                        transform:"crop1",
                        padding:[10,20,40,60]
                  }
               ],
               options:{
                     quality:0.75
               }
            }
          """ 
       When I send request by put
       Then I should see the response code 500     
       And I should see the message:
         """
         {
           "event" : "update or create schema schema1",
           "type" : "ServerError",
           "cause" : "validating parameter however the schema name schema1 in the url is not the same as the name schema2 in the input"
         """
                          
        
     Scenario: display schema
         Given the URL is "media/rest/schemas/schema1"
         When I send request by get         
         Then I should see the message:
         """
         {
           "schema" : "schema1"
         """ 
         
     Scenario: delete schema
         Given the URL is "media/rest/schemas/schema1"
         When I send request by delete         
         Then I should see the response code 204           
                     
           
        
     Scenario: invalid schema when saving schema
     
        Given the schema "schema1" is not existing
        And the URL is "media/rest/schemas"
        When I send request by post
        Then I should see the response code 500
        And I should see the message:
        """
        {
          "event" : "create a new schema",
          "type" : "BadRequest",
          "cause" : "validating parameter however the parameter schema is empty"
        """     
        
        Given the schema "schema1" is not existing
        And the URL is "media/rest/schemas"
        And I input a schema
        """
        {
         schema:"schema1",a
        }
        """
        
        When I send request by post
        Then I should see the response code 500
        Then I should see the message:
        """
        {
          "event" : "create a new schema",
          "type" : "ServerError",
          "cause" : "reading json raw => com.fasterxml.jackson.core.JsonParseException:Unexpected character
        """
        
     Scenario: create schema when putting
        Given the schema "schema1" is not existing
        And the URL is "media/rest/schemas/schema1"
        And I input a schema
          """
             {
               schema:"schema1",
               transforms:[
                  {
                        transform:"crop",
                        padding:[10,20,40,60]
                  }
               ],
               options:{
                     quality:0.75
               }
            }
          """ 
       When I send request by put
       Then I should see the response code 201
        
    Scenario: update existing schema when posting
        Given the URL is "media/rest/schemas/schema1"
        And I input a schema
          """
             {
               schema:"schema1",
               transforms:[
                  {
                        transform:"crop",
                        padding:[10,20,40,60]
                  }
               ],
               options:{
                     quality:0.8
               }
            }
          """ 
       When I send request by put
       Then I should see the response code 204 
       
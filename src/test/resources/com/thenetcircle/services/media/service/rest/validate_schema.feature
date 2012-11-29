Feature: validate schema resource
     
     Scenario: invalid schema
        Given the schema "schema1" is not existing
        And I am on the page "media/rest/schemas/new"
        And I input a schema
        """
           {
             schema:"schema1",
             transforms:[
                {
                      transform:"crop",
                      padding:[10,20,40,10060]
                }
             ],
             options:{
                   quality:0.75
             }
          }
        """
        
        When I save the schema
        Then I should see the message:
        """
        {
          event : "add schema",
          successful : failure
        }
        """
     
      Scenario: schema has variable
      Given the schema "schema1" is not existing
        And I am on the page "media/rest/schemas/new"
        And I input a schema
        """
           {
             schema:"schema1",
             transforms:[
                {
                      transform:"crop",
                      padding:"${padding}"
                }
             ]
          }
        """
        
        When I save the schema
        Then I should see the message:
        """
        {
          event : "add schema",
          successful : true,
          messages : [
            "omit to test the schema"
          ]   
        }
        """
           
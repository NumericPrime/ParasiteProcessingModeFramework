# ParasiteProcessingModeFramework
A framework for creating Java-based-Processing modes that only make small changes to the normal Java Mode.

This framework was created with this template:
https://github.com/processing-mode-template

How to use this Framework:
You need a secondary .jar file placed inside your mode folder.
There needs to be a class extending the Parasite class from the ParasiteMode.jar file.
This class should look something like this:


```java
public class ParasiteExample extends parasite.core.Parasite{
	@Override
	public String[] editCode(String folderName,String sketchName, String[] fileNames, String[] code, boolean[] deleteFile) {
      //Your code
	}
  //This is optional after all changes from editCode are applied the sketch object will be input to the function 
  @Override
  public void editSketch(Object sketch){
    //Your code
  }
}
```
folderName holds the name of the folder in wich the sketch lies.
fileNames holds the name of the code-files in the folder
code holds the code of each file
deleteFile holds the information of wich code-file to ignore. All entries have been set to false. Set entries to true to remove code.


One you are done setting up the class you can open parasite.ini to add the name of the Mode to be shown in the opened Processing window (parasite-name) and the full class name of the parasite class created (class-name)

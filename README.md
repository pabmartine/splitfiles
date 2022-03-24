# splitfiles
Simple application for splitting huge text files (several gigabyte log files) into smaller files manageable by any file viewer

# How to use

java -jar <filename.jar> <path_to_file> <size_in_mb> [allow_headers]

Ej) java -jar splitfiles.jar C:\my_path\my_file.log 100 true

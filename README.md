In order to run this app please substitute your key in build.grade of modulde:app. i.e.replace api_key string value with your generated key as:
buildTypes.each {
        it.buildConfigField 'String', 'API_KEY', "\"api_key\""
    }

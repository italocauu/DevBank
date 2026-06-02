package devbank

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
        
        "/api/$controller"(action: "index", method: "GET")
        "/api/$controller"(action: "save", method: "POST" )

        "/api/$controller/$id" (action: "show", method: "GET")
        "/api/$controller/$id" (action: "update", method: "PUT")
        "/api/$controller/$id" (action: "delete", method: "DELETE")



    }
}

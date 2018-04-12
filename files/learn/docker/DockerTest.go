package main

import (
	"fmt"
	"os"
)

func main() {

	// Docker is an open platform for developers and sysadmins to build, ship, and run distributed applications, whether on laptops, data center VMs, or the cloud.

	fmt.Println(os.Args[1])

	for i := 0; i < 10; i++ {
		fmt.Printf("Hello Docker%d!\n", i)
	}
}
